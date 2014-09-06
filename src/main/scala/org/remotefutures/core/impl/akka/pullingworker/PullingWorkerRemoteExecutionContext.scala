/*
 * Copyright (c) 2014 Martin Senne
 */
package org.remotefutures.core.impl.akka.pullingworker

import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor._
import akka.contrib.pattern.{DistributedPubSubMediator}
import akka.contrib.pattern.DistributedPubSubMediator.Send
import akka.pattern.ask
import org.remotefutures.core.impl.akka.pullingworker.controllers._
import org.remotefutures.core.impl.akka.pullingworker.messages.MasterStatus.{MasterIsNotOperable, MasterOperable, MasterIsOperable, IsMasterOperable}
import org.remotefutures.core.impl.akka.pullingworker.messages._
import org.remotefutures.core._
import scala.concurrent.forkjoin.ThreadLocalRandom
import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.duration._
import akka.util.Timeout


class PullingWorkerRemoteExecutionContext(frontendInformation: FrontEndInformation)
  extends RemoteExecutionContext {

  // TODO: Add consistency check of config
  // val specificSettings = PullingWorkerSettings( settings.specificConfig )

  // val nodeControllers = new PullingWorkerNodeControllers(specificSettings)

  // val frontendInformation = nodeControllers.frontEndController.start(2345)

  def rnd = ThreadLocalRandom.current

  /**
   *
   * @return a new random UUID
   */
  def nextWorkId(): String = UUID.randomUUID().toString

  /**
   * Execute a function in its given context on a distant place.
   *
   * @param body is the code to execute and return T eventually
   * @param bodyContext is the context/closure of of function fnc: () ⇒ T
   * @tparam C specifies the Context type
   * @tparam T specifies the return tyoe
   */
  override def execute[C, T](body: () ⇒ T, bodyContext: C, promise: Promise[T]): Unit = {

    import scala.concurrent.ExecutionContext.Implicits.global

    implicit val timeout = Timeout(5 seconds)

    // construct the message
    val executionMsg = Execute( body )

    // determine system and mediator
    val system = frontendInformation.system
    val mediator = frontendInformation.mediator

    // create fresh actor (which handles master ack and does retries)
    val remoteProducerActor = system.actorOf( Props(classOf[RemoteProducerActor], mediator, promise))

    println("Sending message to remote producer actor " + remoteProducerActor)

    remoteProducerActor ! executionMsg
  }


  /**
   * Reports that an asynchronous computation failed.
   */
  override def reportFailure(cause: Throwable): Unit = {
    // report failure of asynchronous computation
  }


  /**
   * A blocking call, until the system is operable
   */
  override def isOperable(): Unit = {
    import scala.concurrent.duration._
    import akka.util.Timeout


    val timeToWaitForMasterToBecomeOperable = Timeout(100 seconds)

    val system = frontendInformation.system
    val mediator = frontendInformation.mediator

    // create fresh actor (which handles master ack and does retries)
    val pingMasterActor = system.actorOf( Props(classOf[PingMasterActor], mediator))

    println("Message 'IsMasterOperable' is send to ping master actor " + pingMasterActor)

    try {
      val fMasterOperable = pingMasterActor.?(IsMasterOperable)(timeToWaitForMasterToBecomeOperable).mapTo[MasterOperable]

      println("Waiting for final answer from master.")
      val x: MasterOperable = Await.result(fMasterOperable, Duration.Inf)

    } catch {
      // The ping actor hasn't answered within ask timeout [[PingMasterActor.retryInterval]].
      // That means, that the master is not available.
      // This timeout is never thrown by Await.result.
      // Instead the future is completed with a Failure(AskTimeoutException). When calling Await.result this
      //   is thrown as real exception.
      case e: akka.pattern.AskTimeoutException => {
        println("AskTimeoutException")
        throw new Exception("Master is not operable")
      }
    }

  }
}

class PingMasterActor(mediator: ActorRef) extends Actor with ActorLogging {

  import context.dispatcher

  def scheduler = context.system.scheduler

  val retryInterval = 2.seconds

  case object Tick

  override def receive: Actor.Receive = {
    case IsMasterOperable => {
      val originalSender = sender()
      scheduler.scheduleOnce( 1.seconds, self, Tick)
      context.become( waitingForMaster(originalSender) )
    }
  }

  def waitingForMaster(recipient: ActorRef) : Actor.Receive = {
    case MasterIsOperable => { // from master
      log.info("Master is operable")
      recipient ! MasterIsOperable // to caller
      context.stop(self)
    }
    case MasterIsNotOperable => { // from master
      log.info("Master is not operable yet. Retrying in " + retryInterval)
      scheduler.scheduleOnce( retryInterval, self, Tick) // retry
    }
    case Tick => {
      pingMaster
    }
  }
  
  def pingMaster : Unit = {
    mediator ! Send("/user/master/active", IsMasterOperable, localAffinity = false)
  }
}


/**
 * An actor, which is created for each new call to the [[RemoteExecutionContext.execute()]] method.
 *
 * @param mediatorToMaster is the mediator (PubSuis used in n the cluster of frontend nodes and master nod
 *                         to be able to proceed if master actor fails.es)
 * @param promise is the promise which is completed, once the execution is done and the result is present
 */
class RemoteProducerActor(mediatorToMaster: ActorRef, promise: Promise[Any]) extends Actor with ActorLogging {


  // not required anymore
    // register this actor to listen to result topic messages
    // mediatorToMaster ! DistributedPubSubMediator.Subscribe(Master.ResultsTopic, self)


  def scheduler = context.system.scheduler

  def rnd = ThreadLocalRandom.current

  def nextWorkId(): String = UUID.randomUUID().toString

  //  override def preStart(): Unit =
  //  // waiting five whopping seconds before sending a tick???.
  //  // Is there a particular reason for using this strategy?
  //    scheduler.scheduleOnce(5.seconds, self, Tick)

  // override postRestart so we don't call preStart and schedule a new Tick
  override def postRestart(reason: Throwable): Unit = ()

  def receive = {
    case Execute(job) ⇒ {
      log.info("Remote producer actor got Execute message.")

      val work = Work(nextWorkId(), job) // HERE

//      val body: () => Any = work.job
//      log.info("RemoteProducerActor has  {} [{}].", body.hashCode(), body.getClass)
//      val result: Any = body.apply()
//      log.info("RemoteProducerActor has result" + result + " with type " + result.getClass + " and hash " + result.hashCode())

      mediatorToMaster ! Send("/user/master/active", work, localAffinity = false)
      //      (mediatorForMaster ? Send("/user/master/active", work, localAffinity = false)) map {
      //        case Master.Ack(_) ⇒ Frontend.Ok
      //      } recover { case _ ⇒ Frontend.NotOk } pipeTo sender
      context.become(waitForMasterAck(work), discardOld = false)
    }
    case _: DistributedPubSubMediator.SubscribeAck ⇒ {
      log.info("SubscribeAck for actor " + self)
    }
  }

  def waitForMasterAck(work: Work): Actor.Receive = {

    case WorkIsAccepted ⇒
      context.become(waitForWorkResult, discardOld = false)
    // TODO: Error handling ( see FrontEnd.NotOk )

    case WorkResult(workId, result) ⇒
      log.info("Whoops. Received result before Master.Ack. Still consuming result: {}", result)
      promise.success(result)
      log.info("Stopping remote producer actor " + self)
      // 1) context.stop(self)
      context.become(waitForWorkResult, discardOld = false)
  }

  def waitForWorkResult: Actor.Receive = {

    case WorkIsAccepted ⇒
      log.info("And Whoops. Received master ack after work result.")

    case WorkResult(workId, result) ⇒
      log.info("Consuming result: {}", result)
      promise.success(result)
      log.info("Stopping remote producer actor " + self)
      context.stop(self)
  }
}

