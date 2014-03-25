
package org.remotefutures.core.impl

import org.remotefutures.core.RemoteExecutionContext
import scala.concurrent.Future
import org.remotefutures.core.impl.runnable.{PromiseCompletingRunnable, FutureBackedRunnable}

/**
  */
object RemoteFuture {

  //@TODO: Here comes the beef
  def apply[T](body: => T)(implicit executor: RemoteExecutionContext): Future[T] = {

    val runnable = createRunnable()
    executor.execute(runnable)
  }

  // Outline
  private def createRunnable[T](body: => T): FutureBackedRunnable = {

    // why do we need to pass a promise to PromiseCompletingRunnable ?
    // I thought it should create one???
    new PromiseCompletingRunnable(body)

  }
}
