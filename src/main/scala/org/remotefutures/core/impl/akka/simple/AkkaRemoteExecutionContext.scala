/*
 * Copyright (c) 2014 Martin Senne
 */
package org.remotefutures.core.impl.akka.simple

import akka.actor.ActorRef
import org.remotefutures.core.{NodeControllers, RemoteExecutionContext, Settings}
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}
import akka.pattern.ask
import scala.concurrent.duration._
import akka.util.Timeout

class AkkaRemoteExecutionContext(settings : Settings, reporter: Throwable => Unit) extends RemoteExecutionContext {

  /**
   * Execute case class. Used by CallerActor and CalleeActor.
   * @param body is the code to execute on callee (remote node) site.
   */
  case class Execute[T](body: () => T)

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


    val service: ActorRef = null

    implicit val timeout = Timeout(5 seconds)

    val result: Future[Try[T]] = (service ? Execute( () ⇒ body )).asInstanceOf[Future[Try[T]]]

    // Explanation:
    // - The actor, asked by ?, sends back either Success or Failure
    // - onComplete is invoked by Try(M), if the asked actor return message M
    // - The asked actor returns a message Success or Failure
    // -- Thus on successful reception of a message from the actor, onComplete is invoked
    //    either with Success(Success(_)) or
    //    if the actor did not execute successfully with Success(Failure(_))
    // -- if the future (by ?) instead falls into a timeout, we have Failure(_)

    result onComplete {
      case Success(Success(x)) ⇒ {
        println("Success result of ask is " + x)
        promise.success(x)
      }
      case Success(Failure(t)) ⇒ { // problem on worker site ..... not a problem on master site
        println("Failure result of ask is " + t)
        promise.failure(t)
      }
      case Failure(x) ⇒ {
        println("Failure due to other problems ")
        promise.failure(x)
      }
    }
  }




  /**
   * Reports that an asynchronous computation failed.
   */
  override def reportFailure(cause: Throwable): Unit = {

  }

  /**
   * A blocking call, until the system is operable
   */
  override def isOperable(): Unit = ???

}
