/*
 * Copyright (c) 2014 Martin Senne
 */
package org.remotefutures.core.impl.akka.pullingworker

import java.util.UUID

import akka.actor._
import akka.cluster.Cluster
import akka.contrib.pattern.{DistributedPubSubMediator, DistributedPubSubExtension}
import akka.contrib.pattern.DistributedPubSubMediator.Send
import org.remotefutures.core.impl.akka.pullingworker.messages.MasterStatus.{MasterOperable, MasterIsOperable, IsMasterOperable}
import org.remotefutures.core.impl.akka.pullingworker.messages._
import org.remotefutures.core.{RemoteExecutionContext, Settings}
import scala.concurrent.forkjoin.ThreadLocalRandom
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}
import akka.pattern.ask
import scala.concurrent.duration._
import akka.util.Timeout


class PullingWorkerRemoteExecutionContext(settings: Settings, reporter: Throwable ⇒ Unit)
  extends RemoteExecutionContext
  with Startup {

  // =====================================================
  // this is the code to setup other nodes
  // =====================================================
//  println("Starting up pulling worker (final) cluster.")
  val joinAddress = startMaster(None, "backend")
//  Thread.sleep(5000)
//  // startBackend(Some(joinAddress), "backend")
//  // startWorker(joinAddress)
//  startWorker
  // =====================================================

  val frontendSetup: FrontendSetup = new FrontendSetup(joinAddress, masterSystemName)


  // TODO: This is really bloody. We need a mechanism to check, if cluster (master and worker nodes) are up.
  Thread.sleep(5000)

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
    val msg = Execute( body )

    // determine system and mediator
    val system = frontendSetup.system
    val mediator = frontendSetup.mediator

    // create fresh actor (which handles master ack and does retries)
    val remoteProducerActor = system.actorOf( Props(classOf[RemoteProducerActor], mediator, promise))

    println("Sending message to remote producer actor " + remoteProducerActor)

    remoteProducerActor ! msg
  }


  /**
   * Startup the node system
   */
  override def startup(): Unit = {
    // startup cluster
  }

  /**
   * Shutdown the node system
   */
  override def shutdown(): Unit = {
    // shutdown cluster
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

    implicit val timeout = Timeout(5 seconds)

    val mediator = frontendSetup.mediator

    val masterState: Future[MasterOperable] = (mediator ? IsMasterOperable).mapTo[MasterOperable]
  }
}

/**
 *
 * @param joinAddress
 * @param systemName
 */
class FrontendSetup(joinAddress: akka.actor.Address, systemName: String) {
  val system = ActorSystem(systemName)
  Cluster(system).join(joinAddress)
  val mediator = DistributedPubSubExtension(system).mediator
  println("Frontend Setup finished. Mediator is " + mediator)

//   val remoteProducerActor = system.actorOf( Props(classOf[RemoteProducerActor], mediator))

  //  val frontend = actorSystem.actorOf(Props[Frontend], "frontend")
  //  actorSystem.actorOf(Props(classOf[WorkProducer], frontend), "producer")
  //  actorSystem.actorOf(Props[WorkConsumer], "consumer")
}

/**
 *
 * @param mediatorToMaster is the mediator (PubSub in the cluster of frontend nodes and master nodes)
 * @param promise
 */
class RemoteProducerActor(mediatorToMaster: ActorRef, promise: Promise[Any]) extends Actor with ActorLogging {

  // not required anymore
    // register this actor to listen to result topic messages
    // mediatorToMaster ! DistributedPubSubMediator.Subscribe(Master.ResultsTopic, self)

  // private var workerStates = Map[String, Promise[T]]()

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
