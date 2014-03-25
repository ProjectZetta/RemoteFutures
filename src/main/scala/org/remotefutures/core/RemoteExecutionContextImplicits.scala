/*
 * Copyright (c) 2014 Martin Senne, Marvin Hansen.
 */
package org.remotefutures.core

import scala.concurrent.ExecutionContext


/**
 * Implicits for the setup of the environment (remote execution context) of remote futures (RemoteFuture)
 */
object RemoteExecutionContextImplicits {
  /**
   * An remote execution context which uses the configuration given in remotefutures.conf to setup the context.
   */
  implicit val DefaultConfigBasedRemoteExecutionContext: RemoteExecutionContext = {
    RemoteExecutionContext.fromDefaultConfig
  }
}


/**
 * Implicits for the setup of the environment (execution context that is remotable, but NOT derived from RemotExeuctionContext) of regular futures.
 */
object RemoteAwareExecutionContextImplicits {
  implicit val SimpleAkkaRemoteAwareExecutionContext : ExecutionContext = {
    RemoteAwareExecutionContext()
  }
}

