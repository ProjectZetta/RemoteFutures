package org.remotefutures.core.akkabased.simple

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Try, Failure, Success}
import akka.actor._
import akka.pattern.ask
import scala.concurrent.duration._
import akka.util.Timeout
import scala.util.control.NonFatal


/**
 * Actor on caller site, which delegates Execute messages to freshly created remote actors to trigger execution of given body.
 * On receive of Try[T], the underlying promise is completed.
 */
class CallerActor[T](promise: Promise[T]) extends Actor with ActorLogging {
  val callee = context.actorOf(Props[CalleeActor[T]])

  def receive = {
    case msg : Execute[T] ⇒ {
      log.debug("Calling callee with execute msg.")
      callee ! msg
    }
    case t : Try[T] ⇒ {
      log.debug("Caller received Try.")
      promise complete t

      log.debug("Caller will stop (and thus will also stop child callee).")
      context.stop( self )
    }
  }
}

/**
 * An (remote) actor that executes a given body
 */
class CalleeActor[T] extends Actor with ActorLogging {
  def receive = {
    case Execute(body:( () => T )) ⇒ {
      log.debug("Callee received execute.")
      try {
        val result:T = body()
        log.debug("Callee was successful with execution.")
        sender ! Success(result)
        // sender ! result // für apply3
      } catch {
        case NonFatal(e) => {
          log.debug("NonFatal exception on execution.")
          sender ! Failure(e)
        }
      }
    }
  }
}

/**
 * A runnable which performs an ask operation (not tell) onto the given actor {@code callee}, which in detail
 * - sends the body of execution (via an {@link Execute} message) to the actor,
 * - expects an answer from that actor which can be a Try, either
 * -- Success with the result from the execution of body or
 * -- Failure with a Throwable, if the execution of body resulted in an error.
 *
 * The given promise {@code promise} is completed on reception of either Success or Failure from the actor.
 *
 * @param body the fnc to execute
 * @param promise is the promise to put the result of type T into
 * @param caller is the actor, which is "asked" for execution ..... (a.k.a. the master actor)
 * @param ec defines the executioncontext of future completion
 * @tparam T return type of this distributed future.
 */
class ActorAskingPromiseCompletingRunnable[T](body: () ⇒ T, promise: Promise[T], caller: ActorRef, ec: ExecutionContext) extends Runnable {

  implicit final val DBG = true

  override def run() : Unit = {
    implicit val timeout = Timeout(5 seconds)

    implicit val executionContext = ec

    val result: Future[Try[T]] = (caller ? Execute( () ⇒ body )).asInstanceOf[Future[Try[T]]]

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
