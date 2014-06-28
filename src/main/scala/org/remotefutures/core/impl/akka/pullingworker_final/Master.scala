/*
 * Copyright (c) 2014 Martin Senne
 */
package org.remotefutures.core.impl.akka.pullingworker_final

import scala.collection.immutable.Queue
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.contrib.pattern.DistributedPubSubExtension
import akka.contrib.pattern.DistributedPubSubMediator
import akka.contrib.pattern.DistributedPubSubMediator.Put
import scala.concurrent.duration.Deadline
import scala.concurrent.duration.FiniteDuration
import akka.actor.Props

object Master {

  val ResultsTopic = "results"

  def props(workTimeout: FiniteDuration): Props =
    Props(classOf[Master], workTimeout)

  case class Ack(workId: String)

  private sealed trait WorkerStatus
  private case object Idle extends WorkerStatus
  private case class Busy(workAndClient: WorkAndClient, deadline: Deadline) extends WorkerStatus

  private case class WorkerState(ref: ActorRef, status: WorkerStatus)
 
  private case object CleanupTick

  // new case class
  private case class WorkAndClient(client: ActorRef, work: Work)

}

// TODO: modify master, such that
//   - it does not send results via
//     mediator ! DistributedPubSubMediator.Publish(ResultsTopic, WorkResult(workId, result))
//     but instead keeps the actor, which "raised" the unit of work (Work sender)


/**
 * See the sources
 *   - [[http://letitcrash.com/post/29044669086/balancing-workload-across-nodes-with-akka-2]]
 *   - Activator template (Patrik Nordwall)
 *
 * @param workTimeout
 */
class Master(workTimeout: FiniteDuration) extends Actor with ActorLogging {
  import Master._
  import MasterWorkerProtocol._
  val mediator = DistributedPubSubExtension(context.system).mediator

  // in order allow node transparent communication:
  // Allow messages from client, frontend (via PubSub - Send)
  // Mediator Send <-> Mediator Put work hand in hand
  mediator ! Put(self)

  private var workerStates = Map[String, WorkerState]()
  // private var pendingWork = Queue[Work]()
  private var pendingWork = Queue[WorkAndClient]()
  private var workIds = Set[String]()

  import context.dispatcher
  val cleanupTask = context.system.scheduler.schedule(workTimeout / 2, workTimeout / 2, self, CleanupTick)

  override def postStop(): Unit = cleanupTask.cancel()

  def receive = {
    // from frontend, client, whatever.......
    case work: Work ⇒
      // idempotent
      if (workIds.contains(work.workId)) {
        sender ! Master.Ack(work.workId)
      } else {
        log.debug("Accepted work: {}", work)
        // TODO store in Eventsourced
        // pendingWork = pendingWork enqueue work
        pendingWork = pendingWork enqueue WorkAndClient(sender, work)
        workIds += work.workId
        sender ! Master.Ack(work.workId)
        notifyWorkers()
      }

    // from worker
    case RegisterWorker(workerId) ⇒
      if (workerStates.contains(workerId)) {
        workerStates += (workerId -> workerStates(workerId).copy(ref = sender))
      } else {
        log.debug("Worker registered: {}", workerId)
        val worker = sender
        workerStates += (workerId -> WorkerState(worker, status = Idle))
        if (pendingWork.nonEmpty) {
          worker ! WorkNeedsToBeDone
        }
      }

    // from worker
    case RequestForWork(workerId) ⇒
      if (pendingWork.nonEmpty) {
        workerStates.get(workerId) match {
          case Some(s @ WorkerState(_, Idle)) ⇒
            val (workAndClient, rest) = pendingWork.dequeue
            pendingWork = rest
            log.debug("Giving worker {} some work {}", workerId, workAndClient.work.job)
            // TODO store in Eventsourced (now persistence)
            sender ! workAndClient.work
            workerStates += (workerId -> s.copy(status = Busy(workAndClient, Deadline.now + workTimeout)))
          case _ ⇒

        }
      }

    // from worker
    case WorkSuccess(workerId, workId, result) ⇒
      workerStates.get(workerId) match {
        case Some(s @ WorkerState(_, Busy(workAndClient, _))) if workAndClient.work.workId == workId ⇒
          log.debug("Work is done: {} ⇒ {} by worker {}", workAndClient.work, result, workerId)
          // TODO store in Eventsourced
          workerStates += (workerId -> s.copy(status = Idle))

          // TODO ..... remember sender
          // mediator ! DistributedPubSubMediator.Publish(ResultsTopic, WorkResult(workId, result))
          workAndClient.client ! WorkResult(workId, result)

          // sender is the worker, that submitted the successful job
          sender ! MasterWorkerProtocol.WorkStatusAck(workId)
        case _ ⇒
          if (workIds.contains(workId)) {
            // previous Ack was lost, confirm again that this is done
            sender ! MasterWorkerProtocol.WorkStatusAck(workId)
          }
      }

    // from worker
    case WorkFailure(workerId, workId) ⇒
      workerStates.get(workerId) match {
        case Some(s @ WorkerState(_, Busy(workAndClient, _))) if workAndClient.work.workId == workId ⇒
          log.info("Work failed: {}", workAndClient.work)
          // TODO store in Eventsourced
          workerStates += (workerId -> s.copy(status = Idle))
          pendingWork = pendingWork enqueue workAndClient
          notifyWorkers()
        case _ ⇒
      }

      // from self (master)
    case CleanupTick ⇒
      for ((workerId, s @ WorkerState(_, Busy(work, timeout))) <- workerStates) {
        if (timeout.isOverdue) {
          log.info("Work timed out: {}", work)
          // TODO store in Eventsourced
          workerStates -= workerId
          pendingWork = pendingWork enqueue work
          notifyWorkers()
        }
      }
  }

  def notifyWorkers(): Unit =
    if (pendingWork.nonEmpty) {
      // could pick a few random instead of all
      workerStates.foreach {
        case (_, WorkerState(ref, Idle)) ⇒ ref ! WorkNeedsToBeDone
        case _                           ⇒ // busy
      }
    }

  // TODO cleanup old workers
  // TODO cleanup old workIds

}