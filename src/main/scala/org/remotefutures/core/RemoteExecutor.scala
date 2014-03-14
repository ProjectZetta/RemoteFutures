package org.remotefutures.core

import scala.concurrent.Promise

/**
 * A (distributed) remote executor executes a task
 * remotely according to a certain distribution strategy
 * either on a pool of nodes, a specific node or a sub-group
 * of nodes determined by certain properties through a node-selector.
 *
 */
trait RemoteExecutor {
  def execute[C, T](body: () => T, bodyContext: C, promise : Promise[T]): Unit
}
