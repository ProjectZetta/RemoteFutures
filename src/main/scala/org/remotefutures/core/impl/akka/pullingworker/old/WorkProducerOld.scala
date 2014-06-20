package org.remotefutures.core.impl.akka.pullingworker.old

import java.util.UUID
import scala.concurrent.forkjoin.ThreadLocalRandom
import scala.concurrent.duration._
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import org.remotefutures.core.impl.akka.pullingworker.{Frontend, Work}

object WorkProducerOld {
  case object Tick
}

class WorkProducerOld(frontend: ActorRef) extends Actor with ActorLogging {
  import WorkProducerOld._
  import context.dispatcher

  def scheduler = context.system.scheduler
  def rnd = ThreadLocalRandom.current
  def nextWorkId(): String = UUID.randomUUID().toString

  var n = 0

  override def preStart(): Unit =
  // waiting five whopping seconds before sending a tick???.
  // Is there a particular reason for using this strategy?
    scheduler.scheduleOnce(5.seconds, self, Tick)

  // override postRestart so we don't call preStart and schedule a new Tick
  override def postRestart(reason: Throwable): Unit = ()

  def receive = {
    case Tick =>
      n += 1
      log.info("Produced work: {}", n)
      val work = Work(nextWorkId(), n)
      frontend ! work
      context.become(waitAccepted(work), discardOld = false)

  }

  def waitAccepted(work: Work): Actor.Receive = {
    case Frontend.Ok =>
      context.unbecome()
      scheduler.scheduleOnce(rnd.nextInt(3, 10).seconds, self, Tick)
    case Frontend.NotOk =>
      log.info("Work not accepted, retry after a while")
      scheduler.scheduleOnce(3.seconds, frontend, work)
  }

}