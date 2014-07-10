/*
 * Copyright (c) 2014 Martin Senne
 */
package org.remotefutures.core.impl.akka.pullingworker

import java.util.UUID
import scala.concurrent.duration._
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.ReceiveTimeout
import akka.actor.Terminated
import akka.contrib.pattern.ClusterClient.SendToAll
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy.Stop
import akka.actor.SupervisorStrategy.Restart
import akka.actor.ActorInitializationException
import akka.actor.DeathPactException

object Worker {

  def props(clusterClient: ActorRef, workExecutorProps: Props, registerInterval: FiniteDuration = 10.seconds): Props =
    Props(classOf[Worker], clusterClient, workExecutorProps, registerInterval)

}

/**
 * A worker actor runs on a node (not a member of the cluster).
 * A worker actor is in any of the "states"
 *  - '''idle''',
 *  - '''working''' or
 *  - '''waitForWorkIsDoneAck'''
 *
 * Concrete,
 *  - in state '''idle''' it can be
 *    - informed (from master), that new work is available at master via [[MasterWorkerProtocol.WorkNeedsToBeDone]]
 *      in which case it requests work from master via [[MasterWorkerProtocol.RequestForWork]]
 *    - receive concrete new work (from master) via [[org.remotefutures.core.impl.akka.pullingworker.Work]] which is
 *      delegated to the child actor "exec" and then switch to 'working' state.

 *  - in state '''working''' it can be
 *    - informed (from exec actor), that work execution is ready via [[WorkExecutor.WorkComplete]].
 *      It then informs master via [[MasterWorkerProtocol.WorkSuccess]] and changes to __waitForWorkIsDoneAck__ state.
 *
 *  - in state '''waitForWorkIsDoneAck''' it
 *     - waits for acknowledgement from master via [[MasterWorkerProtocol.WorkStatusAck]] in which case
 *       the worker request new work [[MasterWorkerProtocol.RequestForWork]] and becomes idle
 *
 * @param clusterClient
 * @param workExecutorProps
 * @param registerInterval
 */
class Worker(clusterClient: ActorRef, workExecutorProps: Props, registerInterval: FiniteDuration)
  extends Actor with ActorLogging {
  import MasterWorkerProtocol._

  val workerId = UUID.randomUUID().toString

  import context.dispatcher

  // At a regular interval, worker registers at master (via clusterClient ! SendToAll)
  val registerTask = context.system.scheduler.schedule(0.seconds, registerInterval, clusterClient,
    SendToAll("/user/master/active", RegisterWorker(workerId)))

  // Create an child actor "exec", which
  // - performs the real work
  // - is watched (by this actor): The worker actor will receive a Terminated(subject) message when watched actor is terminated.
  val workExecutor = context.watch(context.actorOf(workExecutorProps, "exec"))

  var currentWorkId: Option[String] = None

  def workId: String = currentWorkId match {
    case Some(workId) ⇒ workId
    case None         ⇒ throw new IllegalStateException("Not working")
  }

  // handle errors according to strategy
  override def supervisorStrategy = OneForOneStrategy() {
    // partial function [Throwable ⇒ Directive] (see SupervisorStrategy.Decider)
    case _: ActorInitializationException ⇒ Stop
    case _: DeathPactException           ⇒ Stop
    case _: Exception ⇒
      currentWorkId foreach { workId ⇒ sendToMaster(WorkFailure(workerId, workId)) }
      context.become(idle)
      Restart
  }

  override def postStop(): Unit = registerTask.cancel()

  def receive = idle

  def idle: Receive = {
    case WorkNeedsToBeDone ⇒
      sendToMaster(RequestForWork(workerId))

    case Work(workId, job) ⇒
      // log.info("Got work: {} with type {}", job, job.getClass)
      log.info("Worker got work.")
      currentWorkId = Some(workId)
      // Marvin: While sending work for execution, how do you link the result back to the actual callback?
      // Is it done implicitly through actor reference? Just asking for clarification.

      // 1. delegate work to "exec" actor
      // 2. switch context ( see partial function PartialFunction[Any, Unit] "working" below ):
      // --> This actor (worker) will receive Worker.WorkComplete( result ) from "exec" actor. (
      workExecutor ! job // HERE
      context.become(working)
  }

  def working: Receive = {
    // work is completed on "workExecutor" actor
    case WorkExecutor.WorkComplete(result) ⇒
      log.info("Work is complete. Result {}.", result)
      sendToMaster(WorkSuccess(workerId, workId, result))
      context.setReceiveTimeout(5.seconds)
      context.become(waitForWorkStatusAck(result))

    // Marvin: is queing an option or a bad idea in this case?
    // Martin: Which actor is queueing ? A worker pulls for work. (see waitForWorkIsDoneAck)
    case _: Work ⇒
      log.info("Yikes. Master told me to do work, while I'm working.")
  }

  // Marvin: Am i getting it right, that once the result s back from
  // the exec actor, it's forwarded to the master.
  // Ack ID & workID is matching result to job
  // thus it answers the question asked above...
  // Martin: Nearly
  //   1.a)  MasterWorkerProtocol.WorkSuccess is sent to master (see working(...) )
  //         MasterWorkerProtocol.WorkFailure is sent to master in case of an exception (see supervisorStrategy()
  //     b)  Master actor acknowledges with MasterWorkerProtocol.WorkStatusAck (see below )
  //   2. MasterWorkerProtocol.RequestForWork is sent to master (see below) to request more work

  def waitForWorkStatusAck(result: Any): Receive = {
    case WorkStatusAck(id) if id == workId ⇒
      sendToMaster(RequestForWork(workerId))
      context.setReceiveTimeout(Duration.Undefined)
      context.become(idle)
    case ReceiveTimeout ⇒
      log.info("No ack from master, retrying")
      sendToMaster(WorkSuccess(workerId, workId, result))
  }

  override def unhandled(message: Any): Unit = message match {
    case Terminated(`workExecutor`) ⇒ context.stop(self)
    case WorkNeedsToBeDone          ⇒
    case _                          ⇒ super.unhandled(message)
  }

  def sendToMaster(msg: Any): Unit = {
    clusterClient ! SendToAll("/user/master/active", msg)
  }

}