package org.remotefutures.core.impl.executor

import org.remotefutures.core.RemoteExecutor
import scala.concurrent.{ExecutionContext, Promise}
import scala.util.{Failure, Success}
import scala.util.control.NonFatal

/**
 * A dummy remote executor which runs the function body in a Runnable on the local ExecutionContext
 */
class LocalRunnableRemoteExecutor extends RemoteExecutor {

  override def execute[C, T](body: () => T, bodyContext: C, promise: Promise[T]): Unit = {
    println("This is execute of LocalRunnableRemoteExecutor.")

    import scala.concurrent.ExecutionContext.Implicits.global

    implicit val executor: ExecutionContext = global

    val runnable = new PromiseCompletingRunnable(body, promise)
    executor.prepare.execute(runnable)
  }

  /**
   * A runnable wrapping the execution of body, whose result is put the a promise.
   *
   * Shamlessly copied from concurrent.impl.Future and modified.
   *
   * @param body the body to execute
   * @param promise is the promise to put the result of type T into
   * @tparam T return type of this distributed future.
   */
  class PromiseCompletingRunnable[T](body: () => T, promise : Promise[T]) extends Runnable {
    override def run() = {
      promise complete {
        try {
          println("Before execution of body on " + this)
          val result: T = body() // execute body
          println("After execution of body on " + this)
          Success(result)

        } catch {
          case NonFatal(e) => Failure(e)
        }
      }
    }
  }
}
