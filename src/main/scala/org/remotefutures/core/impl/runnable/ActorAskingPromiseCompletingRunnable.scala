package org.remotefutures.core.impl.runnable
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Try, Failure, Success}
import akka.actor._
import akka.pattern.ask
import scala.concurrent.duration._
import akka.util.Timeout


/**
 * A runnable wrapping the execution of fnc, whose result is put the a promise.
 *
 * Shamelessly copied from concurrent.impl.Future and modified.
 *
 * @param body the fnc to execute
 * @param promise is the promise to put the result of type T into
 * @tparam T return type of this distributed future.
 */
private[impl] class ActorAskingPromiseCompletingRunnable[T](body: () => T, promise: Promise[T], callee: ActorRef, ec: ExecutionContext) extends FutureBackedRunnable {

  implicit final val DBG = true

  implicit val executionContext = ec

  override def run() : Unit = {
    implicit val timeout = Timeout(5 seconds)

    val result: Future[Try[T]] = (callee ? Execute( () ⇒ body )).asInstanceOf[Future[Try[T]]]

    // Explanation:
    // - The actor, asked by ?, sends back either Success or Failure
    // - If the "asked" actor would return a message M, then onComplete would be invoked by either Success(M) or Failure(_) if the actor would not respond
    // - Since the actor return a message Success or Failure
    // -- on successful reception of a message from the actor, onComplete is invoked
    //    either with Success(Success(_)) or
    //    if the actor did not execute successfully with Success(Failure(_))
    // -- if the future (by ?) instead falls into a timeout, we have Failure(_)

    result onComplete {
      case Success(Success(x)) ⇒ {
        println("Success result of ask is " + x)
        promise.success(x)
      }
      case Success(Failure(t)) ⇒ {
        println("Failure result of ask is " + t)
        promise.failure(t)
      }
      case Failure(x) ⇒ {
        println("Failure due to other reasons")
        promise.failure(x)
      }
    }
  }
}

/**
 * Execute case class. Used by CallerActor and CalleeActor.
 * @param body is the code to execute on callee (remote node) site.
 */
case class Execute[T](body: () => T)
