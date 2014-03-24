package org.remotefutures.core

import scala.concurrent.ExecutionContext

object RemoteAwareExecutionContext {
  def apply() : ExecutionContext = {
    new SimpleAkkaRemoteAwareExecutionContext
  }
}

// IMPLEMENTATION follows below



class SimpleAkkaRemoteAwareExecutionContext extends ExecutionContext {

  // setup the akkabased system

  override def execute(runnable: Runnable): Unit = {

  }

  override def reportFailure(cause: Throwable): Unit = ???

}
