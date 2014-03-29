/*
 * Copyright (c) 2014 Martin Senne, Marvin Hansen.
 */
package org.remotefutures.proposals.idea_runnable
import scala.concurrent.{ExecutionContext, Promise, Future}
import org.remotefutures.core.{PromiseBackedRunnable, impl, RemoteExecutionContext}
import org.remotefutures.core.impl.PromiseCompletingRunnable

object RemoteFuture_Suggestion {

  def apply[T](body: => T)(implicit executor: ExecutionContext): scala.concurrent.Future[T] = {

    val runnable = createRunnable( body )
    executor.prepare.execute( runnable )
    runnable.promise.future
  }

  private def createRunnable[T](body: => T): PromiseBackedRunnable[T] = {

    // why do we need to pass a promise to PromiseCompletingRunnable ?
    // I thought it should create one???
    // Martin: Because, we need to return a Future on apply ..... where else should we get it?
    val promise = Promise[T]()
    new PromiseCompletingRunnable( () => body, promise )
  }
}


