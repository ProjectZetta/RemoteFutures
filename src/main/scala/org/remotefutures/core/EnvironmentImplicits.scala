/*
* Copyright (c) 2014 Martin Senne, Marvin Hansen.
*/
package org.remotefutures.core

/**
 * Implicits for the setup of the environment of remote futures.
 */
object EnvironmentImplicits {
  /**
   * An remote execution context which uses the configuration given in remotefutures.conf to setup the context.
   */
  implicit val DefaultConfigBasedRemoteExecutionContext: RemoteExecutionContext = {
    RemoteExecutionContext.fromDefaultConfig
  }
}