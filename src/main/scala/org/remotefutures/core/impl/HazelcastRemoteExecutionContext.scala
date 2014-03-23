package org.remotefutures.core.impl

import org.remotefutures.core.{RemoteExecutionContext, Settings}
import scala.concurrent.Promise


private[core] class HazelcastRemoteExecutionContext private[impl](settings: Settings, reporter: Throwable => Unit) extends RemoteExecutionContext {
  /**
   * Shutdown the node system
   */
  override def shutdown(): Unit = ???

  /**
   * Startup the node system
   */
  override def startup(): Unit = ???

  /**
   * Reports that an asynchronous computation failed.
   */
  override def reportFailure(cause: Throwable): Unit = ???

  /**
   * Execute a function in its given context on a distant place.
   *
   * @param body is the code to execute and return T eventually
   * @param bodyContext is the context/closure of of function fnc: () => T
   * @tparam C specifies the Context type
   * @tparam T specifies the return tyoe
   */
  override def execute[C, T](body: () => T, bodyContext: C, promise: Promise[T]): Unit = ???
}
