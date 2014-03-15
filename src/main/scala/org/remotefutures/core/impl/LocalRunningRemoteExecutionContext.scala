/*
 * Copyright (c) 2014 Martin Senne, Marvin Hansen.
 */
package org.remotefutures.core.impl

import org.remotefutures.core.{RemoteExecutor, RemoteExecutionContext, Settings}
import org.remotefutures.core.impl.executor.LocalRunningRemoteExecutor
import scala.concurrent.Promise

private[core] class LocalRunningRemoteExecutionContext  private[impl] (settings : Settings, reporter: Throwable => Unit) extends RemoteExecutionContext {

  /**
   * Facility to create a RemoteExecutor used in the context
   * in case none is given. The actual point is providing
   * some kind of near-zero overhead default RemoteExecutor
   *
   * @return RemoteExecutor
   */
  val executor: RemoteExecutor = new LocalRunningRemoteExecutor

  override def execute[C, T](body: () => T, bodyContext: C, promise: Promise[T]): Unit = {
    // call should be something like executor.execute(fnc,fncContext)
    // This goes hand in hand with the interface definition of RemoteExecutor
    // already suggested.
    executor.execute(body, bodyContext, promise)
  }

  override def reportFailure(t: Throwable) = reporter(t)

  override def shutdown(): Unit = ???

  override def startup(): Unit = ???
}
