/*
 * Copyright (c) 2014 Martin Senne
 */
package org.remotefutures.core.impl.akka.pullingworker

import java.util.UUID

import akka.actor.{Props, ActorSystem, ActorRef}
import akka.cluster.Cluster
import akka.contrib.pattern.DistributedPubSubExtension
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

  /**
   * Execute case class. Used by CallerActor and CalleeActor.
   * @param body is the code to execute on callee (remote node) site.
   */
  case class Execute[T](body: () => T)

  // =====================================================
  // this will be executed on different nodes
  // =====================================================
  println("Starting up pulling worker cluster.")
  val joinAddress = startBackend(None, "backend")
  Thread.sleep(5000)
  // startBackend(Some(joinAddress), "backend")
  startWorker(joinAddress)
  // =====================================================

  val frontEnd: FrontEnd = new FrontEnd(joinAddress, systemName)

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

    // val result: Future[Try[T]] = (service ? Execute( () ⇒ body )).asInstanceOf[Future[Try[T]]]


    val work = Work(nextWorkId(), 1)
    val r: Future[Any] = frontEnd.mediator ? Send("/user/master/active", work, localAffinity = false)

    r onComplete {
      case x ⇒ {
        println("Job transmitted result: " + x + " with Class " + x.getClass)
      }
    }

    // r.zip()


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

class FrontEnd(joinAddress: akka.actor.Address, systemName: String) {
  val system = ActorSystem(systemName)
  Cluster(system).join(joinAddress)
  val mediator = DistributedPubSubExtension(system).mediator

  //  val frontend = actorSystem.actorOf(Props[Frontend], "frontend")
  //  actorSystem.actorOf(Props(classOf[WorkProducer], frontend), "producer")
  //  actorSystem.actorOf(Props[WorkConsumer], "consumer")
}




