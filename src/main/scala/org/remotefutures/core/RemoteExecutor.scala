/* Copyright (c) 2014 Marvin Hansen.
 * www.marvin-hansen.tel.
 * ALl RIGHTS RESERVED
 ***************************
 * Project: DistributedRemoteFutures
 * User: Marvin Hansen
 * Web: www.marvin-hansen.tel
 * Date: 3/11/14 (/dd/mm/yy)
 * Time: 6:32 PM (CET)
 */
package org.remotefutures.core

import org.remotefutures.network.Node

/**
 *
 * A RemoteExecutor executes a task remotely.
 *
 */
//* companion object for static access i.e. imports */
object RemoteExecutor extends RemoteExecutor

// *composition trait, matches interface to implementation by name*/
trait RemoteExecutor extends RemoteExecutorComponent with RemoteExecutorComponentImpl

//** Interface */
trait RemoteExecutorComponent {

  protected val remoteExecutionService: RemoteExecutionService

  protected trait RemoteExecutionService {

    // Executes on any node on a pool
    def executeOnPool[T](task: PromiseCompletingRunnable[T])

    // Executes on a specific node on a pool i.e. GPU node
    def executeOnNode[T](task: PromiseCompletingRunnable[T], node: Node)

    // Load balanced execution
    def executeLoadBalanced[T](task: PromiseCompletingRunnable[T])

    // Fail-over execution
    def executeLoadFailOver[T](task: PromiseCompletingRunnable[T])

  }

}

//*  Sample Implementation */
trait RemoteExecutorComponentImpl extends RemoteExecutorComponent {

  override protected val remoteExecutionService: RemoteExecutionService = new RemoteExecutionServiceImpl

  private[this] class RemoteExecutionServiceImpl extends RemoteExecutionService {
    // Executes on pool using a specific distribution strategy
    override def executeLoadBalanced[T](task: PromiseCompletingRunnable[T]): Unit = ???

    // Executes on a specific node on a pool i.e. GPU node
    override def executeOnNode[T](task: PromiseCompletingRunnable[T], node: Node): Unit = ???

    // Executes on any node on a pool
    override def executeOnPool[T](task: PromiseCompletingRunnable[T]): Unit = ???

    override def executeLoadFailOver[T](task: PromiseCompletingRunnable[T]) = ???
  }

}


