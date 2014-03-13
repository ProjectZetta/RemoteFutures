/*
 * Copyright (c) 2014 Martin Senne, Marvin Hansen.
 */
package org.remotefutures.core


//Suggesting an interface  that extends Executor, for instance:
//import java.util.concurrent.Executor

//trait RemoteExecutor extends Executor
// with an companion oject, just like the one below.

//Then, the actual implementation could be something like
//class akkaRemoteExecutor extends RemoteExecutor {}

// so that the companion object is used to switch implementation,
//according to whatever criterion.



object RemoteExecutor {
  def apply(config : Config) : RemoteExecutor = {
    def createInstance[T](fqn: String) : T = {
      Class.forName( fqn ).newInstance().asInstanceOf[T]
    }

    createInstance[RemoteExecutor]( config.remoteExecutorClassname )
  }
}



/**
 * A (distributed) remote executor executes a task
 * remotely according to a certain distribution strategy
 * either on a pool of nodes, a specific node or a sub-group
 * of nodes determined by certain properties through a node-selector.
 *
 */
<<<<<<< HEAD
object RemoteExecutor {
  def fromConfig(config: RemoteConfig): RemoteExecutor = {
    new RemoteExecutor
  }
=======
trait RemoteExecutor {
  def execute[C, T](body: () => T, bodyContext: C): Unit
>>>>>>> 9044e427f902483f38cb126dc8304f935794172f
}

/**
 * A dummy remote executor implementation
 */
class DummyRemoteExecutor extends RemoteExecutor {

  override def execute[C, T](body: () => T, bodyContext: C): Unit = {
    println("This is execute")
  }
}

