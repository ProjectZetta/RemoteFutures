package org.remotefutures.core.impl.akkaactor.worker

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

  case class WorkComplete(result: Any)
}

class Worker(clusterClient: ActorRef, workExecutorProps: Props, registerInterval: FiniteDuration)
  extends Actor with ActorLogging {
  import Worker._
  import MasterWorkerProtocol._

  val workerId = UUID.randomUUID().toString

  import context.dispatcher
  val registerTask = context.system.scheduler.schedule(0.seconds, registerInterval, clusterClient,
    SendToAll("/user/master/active", RegisterWorker(workerId)))

  // I guess that is the actual worker executing the job/task
  val workExecutor = context.watch(context.actorOf(workExecutorProps, "exec"))

  var currentWorkId: Option[String] = None
  def workId: String = currentWorkId match {
    case Some(workId) => workId
    case None         => throw new IllegalStateException("Not working")
  }

  // what is OneForOneStrategy?
  // Ok, have read the Akka manual, its clear now.
  override def supervisorStrategy = OneForOneStrategy() {
    case _: ActorInitializationException => Stop
    case _: DeathPactException           => Stop
    case _: Exception =>
      currentWorkId foreach { workId => sendToMaster(WorkFailed(workerId, workId)) }
      context.become(idle)
      Restart
  }

  override def postStop(): Unit = registerTask.cancel()

  def receive = idle

  def idle: Receive = {
    case WorkIsReady =>
      sendToMaster(WorkerRequestsWork(workerId))

    case Work(workId, job) =>
      log.debug("Got work: {}", job)
      currentWorkId = Some(workId)
      // Marvin: While sending work for execution,
      // how do you link the result back to the actual callback?
      // Is it done implicitly through actor reference?
      // Just asking for clarification.
      workExecutor ! job
      context.become(working)
  }

  def working: Receive = {
    case WorkComplete(result) =>
      log.debug("Work is complete. Result {}.", result)
      sendToMaster(WorkIsDone(workerId, workId, result))
      context.setReceiveTimeout(5.seconds)
      context.become(waitForWorkIsDoneAck(result))

    //Marvin: is queing an option or a bad idea in this case?
    case _: Work =>
      log.info("Yikes. Master told me to do work, while I'm working.")
  }

  // Marvin:
  // Am i getting it right, that once the result s back from
  // the executor, it's forwarded to the master.
  // Ack ID & workID is matching result to job
  // thus it answers the question asked above...
  def waitForWorkIsDoneAck(result: Any): Receive = {
    case Ack(id) if id == workId =>
      sendToMaster(WorkerRequestsWork(workerId))
      context.setReceiveTimeout(Duration.Undefined)
      context.become(idle)
    case ReceiveTimeout =>
      log.info("No ack from master, retrying")
      sendToMaster(WorkIsDone(workerId, workId, result))
  }

  override def unhandled(message: Any): Unit = message match {
    case Terminated(`workExecutor`) => context.stop(self)
    case WorkIsReady                =>
    case _                          => super.unhandled(message)
  }

  def sendToMaster(msg: Any): Unit = {
    clusterClient ! SendToAll("/user/master/active", msg)
  }

}