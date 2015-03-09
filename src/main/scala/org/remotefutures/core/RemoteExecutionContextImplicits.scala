/*
 * Copyright (c) 2014 Martin Senne.
 */
package org.remotefutures.core


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

