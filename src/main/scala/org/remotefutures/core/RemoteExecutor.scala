/*
 * Copyright (c) 2014 Martin Senne, Marvin Hansen.
 */
package org.remotefutures.core

import org.remotefutures.core.DistributionStrategy._
import org.remotefutures.network.{NodeSelector, Node}

/**
 * A (distributed) remote executor executes a task
 * remotely according to a certain distribution strategy
 * either on a pool of nodes, a specific node or a sub-group
 * of nodes determined by certain properties through a node-selector.
 *
 */
object RemoteExecutor {
  def fromConfig( config : RemoteConfig) : RemoteExecutor = {
    new RemoteExecutor
  }
}

/**
 *
 */
class RemoteExecutor

