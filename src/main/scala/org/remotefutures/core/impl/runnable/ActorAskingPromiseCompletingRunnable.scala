package org.remotefutures.core.impl.runnable
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Try, Failure, Success}
import akka.actor._
import akka.pattern.ask
import scala.concurrent.duration._
import akka.util.Timeout


/**
 * A runnable which performs an ask (not tell) an the given actor {@code callee], which in detail
 * - sends the body of execution (via an {@link Execute} message) to the actor,
 * - expects an answer from that actor which can be either
 * -- Success with the result from the execution of body
 * -- Failure with a Throwable, if the execution of body resulted in an error.
 *
 * The given promise {@code promise} is completed on reception of either Success or Failure from the actor.
 *
 * @param body the fnc to execute
 * @param promise is the promise to put the result of type T into
 * @param callee is the actor, which is "asked".
 * @param ec defines the executioncontext of future completion
 * @tparam T return type of this distributed future.
 */
private[impl] class ActorAskingPromiseCompletingRunnable[T](body: () => T, promise: Promise[T], callee: ActorRef, ec: ExecutionContext) extends Runnable {

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
