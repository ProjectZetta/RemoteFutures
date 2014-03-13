/*
 * Copyright (c) 2014 Martin Senne, Marvin Hansen.
 */
package org.remotefutures.core


object RemoteExecutor {
  def apply(config: Config): RemoteExecutor = {
    def createInstance[T](fqn: String): T = {
      Class.forName(fqn).newInstance().asInstanceOf[T]
    }

    createInstance[RemoteExecutor](config.remoteExecutorClassname)
  }
}


/**
 * A (distributed) remote executor executes a task
 * remotely according to a certain distribution strategy
 * either on a pool of nodes, a specific node or a sub-group
 * of nodes determined by certain properties through a node-selector.
 *
 */
trait RemoteExecutor {
  def execute[C, T](body: () => T, bodyContext: C): Unit
}

/**
 * A dummy remote executor implementation
 */
class DummyRemoteExecutor extends RemoteExecutor {

  override def execute[C, T](body: () => T, bodyContext: C): Unit = {
    println("This is execute")
  }
}

