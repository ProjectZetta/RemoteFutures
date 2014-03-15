package org.remotefutures.core.impl.executor

import org.remotefutures.core.RemoteExecutor
import scala.concurrent.{ExecutionContext, Promise}
import scala.util.{Failure, Success}
import scala.util.control.NonFatal

/**
 * An remote executor which runs the function fnc in a Runnable on the local ExecutionContext
 */
class LocalRunningRemoteExecutor extends RemoteExecutor {

  override def execute[C, T](fnc: () => T, fncContext: C, promise: Promise[T]): Unit = {
    println("This is execute of LocalRunnableRemoteExecutor.")

    import scala.concurrent.ExecutionContext.Implicits.global

    implicit val executor: ExecutionContext = global

    val runnable = new PromiseCompletingRunnable(fnc, promise)
    executor.prepare.execute(runnable)
  }

  /**
   * A runnable wrapping the execution of fnc, whose result is put the a promise.
   *
   * Shamelessly copied from concurrent.impl.Future and modified.
   *
   * @param body the fnc to execute
   * @param promise is the promise to put the result of type T into
   * @tparam T return type of this distributed future.
   */
  class PromiseCompletingRunnable[T](body: () => T, promise : Promise[T]) extends Runnable {
    override def run() = {
      promise complete {
        try {
          println("Before execution of fnc on " + this)
          val result: T = body() // execute fnc
          println("After execution of fnc on " + this)
          Success(result)

        } catch {
          case NonFatal(e) => Failure(e)
        }
      }
    }
  }
}
