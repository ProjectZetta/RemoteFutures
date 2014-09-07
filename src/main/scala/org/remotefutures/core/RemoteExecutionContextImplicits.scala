/*
 * Copyright (c) 2014 Martin Senne.
 */
package org.remotefutures.core

import scala.concurrent.ExecutionContext
import org.remotefutures.proposals.idea_specialized_executioncontext.RemoteAwareExecutionContext


/**
 * Implicits for the setup of the environment (remote execution context) of remote futures (RemoteFuture)
 */
object RemoteExecutionContextImplicits {
  /**
   * An remote execution context which uses the configuration given in remotefutures.conf to setup the context.
   */
  implicit val defaultConfigBasedRemoteExecutionContext: RemoteExecutionContext = {
    RemoteExecutionContext.fromDefaultConfig
  }
}


/**
 * Implicits for the setup of the environment (execution context that is remotable, but NOT derived from RemotExecutionContext) of regular futures.
 */
object RemoteAwareExecutionContextImplicits {
  implicit val SimpleAkkaRemoteAwareExecutionContext : ExecutionContext = {
    RemoteAwareExecutionContext()
  }
}

