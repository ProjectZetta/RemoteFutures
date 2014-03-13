package org.remotefutures.core.impl

import org.remotefutures.core.{RemoteExecutionContext, RemoteExecutor}

private[core] class RemoteExecutionContextImpl private[impl] (res: RemoteExecutor, reporter: Throwable => Unit) extends RemoteExecutionContext {


  val executor: RemoteExecutor = res match {
    case null => createRemoteExecutorService
    case some => some
  }

  /**
   * Facility to create a RemoteExecutor used in the context
   * in case none is given. The actual point is providing
   * some kind of near-zero overhead default RemoteExecutor
   *
   * @return RemoteExecutor
   */
  def createRemoteExecutorService: RemoteExecutor = ???


  override def execute[C, T](body: () => T, bodyContext: C): Unit = {
    // call should be something like executor.execute(body,bodyContext)
    // This goes hand in hand with the interface definition of RemoteExecutor
    // already suggested.
  }

  override def reportFailure(t: Throwable) = reporter(t)

}

private[core] object RemoteExecutionContextImpl {
  def fromRemoteExecutor(e: RemoteExecutor, reporter: Throwable => Unit = RemoteExecutionContext.defaultReporter): RemoteExecutionContextImpl = {


    new RemoteExecutionContextImpl(e, reporter)
  }
}