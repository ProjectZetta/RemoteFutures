package org.remotefutures.core.impl

import scala.concurrent.Promise

/**
 * A (distributed) remote executor executes a task
 * remotely according to a certain distribution strategy
 * either on a pool of nodes, a specific node or a sub-group
 * of nodes determined by certain properties through a node-selector.
 */
trait RemoteExecutor {

  /**
   * Execute function {@code fnc} with its context / closure given as {@code fncContext} remotely and
   * write the result of this execution to the given promise {@promise}.
   * @param fnc is the function to execute remotely
   * @param fncContext is the context of that function. Spoken differently, it's closure. Later, Here Spores (SIP-21) comes into play
   * @param promise the promise to write the result to
   * @tparam C type of function context
   * @tparam T return value of function
   */
  def execute[C, T](fnc: () => T, fncContext: C, promise : Promise[T]): Unit
}
