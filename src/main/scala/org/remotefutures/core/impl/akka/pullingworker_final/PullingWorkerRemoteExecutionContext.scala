/*
 * Copyright (c) 2014 Martin Senne
 */
package org.remotefutures.core.impl.akka.pullingworker_final

import java.util.UUID

import akka.actor._
import akka.cluster.Cluster
import akka.contrib.pattern.{DistributedPubSubMediator, DistributedPubSubExtension}
import akka.contrib.pattern.DistributedPubSubMediator.Send
import org.remotefutures.core.{RemoteExecutionContext, Settings}
import scala.concurrent.forkjoin.ThreadLocalRandom
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}
import akka.pattern.ask
import scala.concurrent.duration._
import akka.util.Timeout




class PullingWorkerRemoteExecutionContext(settings: Settings, reporter: Throwable => Unit)
  extends RemoteExecutionContext
  with Startup {

  // =====================================================
  // this is the code to setup other nodes
  // =====================================================
  println("Starting up pulling worker cluster.")
  val joinAddress = startBackend(None, "backend")
  Thread.sleep(5000)
  // startBackend(Some(joinAddress), "backend")
  startWorker(joinAddress)
  // =====================================================

  val frontendSetup: FrontendSetup = new FrontendSetup(joinAddress, systemName)
  // TODO: This is really bloody. We need a mechanism to check, if cluster (master and worker nodes) is up.
  Thread.sleep(5000)

  def rnd = ThreadLocalRandom.current
  def nextWorkId(): String = UUID.randomUUID().toString

  /**
   * Execute a function in its given context on a distant place.
   *
   * @param body is the code to execute and return T eventually
   * @param bodyContext is the context/closure of of function fnc: () => T
   * @tparam C specifies the Context type
   * @tparam T specifies the return tyoe
   */
  override def execute[C, T](body: () => T, bodyContext: C, promise: Promise[T]): Unit = {

    import scala.concurrent.ExecutionContext.Implicits.global

    implicit val timeout = Timeout(5 seconds)

    val msg = Execute( () => body )
    val possibleResult: Future[T] = (frontendSetup.remoteProducerActor ? msg).asInstanceOf[Future[T]]

    // complete the promise
    possibleResult.onComplete {
      case x ⇒ {
        promise.complete(x)
      }
    }


    // old 3 =====================================================
//    val remoteProducerActor = frontendSetup.system.actorOf(
//      Props(classOf[RemoteProducerActor], frontendSetup.mediator, promise))
//
//    println("Sending execute to actor " + remoteProducerActor)
//    remoteProducerActor ! Execute( () => body )


    // old 2 =====================================================
//    val work = Work(nextWorkId(), 1)
//    val r: Future[Any] = frontEnd.mediator ? Send("/user/master/active", work, localAffinity = false)
//
//    r onComplete {
//      case x ⇒ {
//        println("Job transmitted result: " + x + " with Class " + x.getClass)
//      }
//    }


    // old 1 (very old )=====================================================
    // val result: Future[Try[T]] = (service ? Execute( () ⇒ body )).asInstanceOf[Future[Try[T]]]


    // Explanation:
    // - The actor, asked by ?, sends back either Success or Failure
    // - onComplete is invoked by Try(M), if the asked actor return message M
    // - The asked actor returns a message Success or Failure
    // -- Thus on successful reception of a message from the actor, onComplete is invoked
    //    either with Success(Success(_)) or
    //    if the actor did not execute successfully with Success(Failure(_))
    // -- if the future (by ?) instead falls into a timeout, we have Failure(_)
//    result onComplete {
//      case Success(Success(x)) ⇒ {
//        println("Success result of ask is " + x)
//        promise.success(x)
//      }
//      case Success(Failure(t)) ⇒ {
//        // problem on worker site ..... not a problem on master site
//        println("Failure result of ask is " + t)
//        promise.failure(t)
//      }
//      case Failure(x) ⇒ {
//        println("Failure due to other problems ")
//        promise.failure(x)
//      }
//    }
  }


  /**
   * Startup the node system
   */
  override def startup(): Unit = {

    // Thread.sleep(5000)
    // startFrontend(joinAddress)
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
  val remoteProducerActor = system.actorOf( Props(classOf[RemoteProducerActor], mediator))

  //  val frontend = actorSystem.actorOf(Props[Frontend], "frontend")
  //  actorSystem.actorOf(Props(classOf[WorkProducer], frontend), "producer")
  //  actorSystem.actorOf(Props[WorkConsumer], "consumer")
}

/**
 *
 * @param mediatorToMaster
 * @param promise
 */
class RemoteProducerActor(mediatorToMaster: ActorRef, promise: Promise[Any]) extends Actor with ActorLogging {
  import org.remotefutures.core.impl.akka.pullingworker.PullingWorkerRemoteExecutionContext.Execute

  // register this actor to listen to result topic messages
  mediatorToMaster ! DistributedPubSubMediator.Subscribe(Master.ResultsTopic, self)

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
    case Execute(body) => {
      log.info("Remote producer actor got Execute.")
      val work = Work(nextWorkId(), body)
      mediatorToMaster ! Send("/user/master/active", work, localAffinity = false)
      //      (mediatorForMaster ? Send("/user/master/active", work, localAffinity = false)) map {
      //        case Master.Ack(_) => Frontend.Ok
      //      } recover { case _ => Frontend.NotOk } pipeTo sender
      context.become(waitForMasterAck(work), discardOld = false)
    }
    case _: DistributedPubSubMediator.SubscribeAck => {
      log.info("SubscribeAck for actor " + self)
    }
  }

  def waitForMasterAck(work: Work): Actor.Receive = {
    case Master.Ack =>
      context.become(waitForWorkResult, discardOld = false)
    // TODO: Error handling ( see FrontEnd.NotOk )
    case WorkResult(workId, result) =>
      log.info("Whoops. Received result before Master.Ack. Still consuming result: {}", result)
      promise.success(result)
      log.info("Stopping remote producer actor " + self)
      // 1) context.stop(self)
      context.become(waitForWorkResult, discardOld = false)
  }

  def waitForWorkResult: Actor.Receive = {
    case Master.Ack =>
      log.info("And Whoops. Received master ack after work result.")
    case WorkResult(workId, result) =>
      log.info("Consuming result: {}", result)
      promise.success(result)
      log.info("Stopping remote producer actor " + self)
      context.stop(self)
  }
}
