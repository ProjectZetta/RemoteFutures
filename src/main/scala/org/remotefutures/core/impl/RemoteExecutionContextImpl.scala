package org.remotefutures.core.impl

import org.remotefutures.core.{RemoteExecutionContext, RemoteExecutor}

private[core] class RemoteExecutionContextImpl private[impl] (res: RemoteExecutor, reporter: Throwable => Unit) extends RemoteExecutionContext {

  override def execute[C, T](body: () => T, bodyContext: C): Unit = {
    // here comes invocation of res
  }

  override def reportFailure(t: Throwable) = reporter(t)

}

private[core] object RemoteExecutionContextImpl {
  def fromRemoteExecutor(e: RemoteExecutor, reporter: Throwable => Unit = RemoteExecutionContext.defaultReporter): RemoteExecutionContextImpl = {
    new RemoteExecutionContextImpl(e, reporter)
  }
}