/*
 * Copyright (c) 2014 Martin Senne, Marvin Hansen.
 */
package org.remotefutures.core.impl.local

import org.remotefutures.core.{RemoteExecutionContext, Settings}
import scala.concurrent.Promise
import org.remotefutures.core.impl.RemoteExecutor


private[core] class LocalRunningRemoteExecutionContext(settings : Settings, reporter: Throwable => Unit) extends RemoteExecutionContext {

  /**
   * Facility to create a RemoteExecutor used in the context
   * in case none is given. The actual point is providing
   * some kind of near-zero overhead default RemoteExecutor
   *
   * @return RemoteExecutor
   */
  val executor: RemoteExecutor = new LocalRunningRemoteExecutor

  /**
   * Execute a function in its given context on a distant place.
   *
   * @param body is the code to execute and return T eventually
   * @param bodyContext is the context/closure of of function fnc: () => T
   * @tparam C specifies the Context type
   * @tparam T specifies the return tyoe
   */
  override def execute[C, T](body: () => T, bodyContext: C, promise: Promise[T]): Unit = {
    executor.execute(body, bodyContext, promise)
  }

  override def reportFailure(cause: Throwable) = reporter(cause)

  override def startup(): Unit = {
    // nothing to do
  }

  override def shutdown(): Unit = {
    // nothing to do
  }
}

