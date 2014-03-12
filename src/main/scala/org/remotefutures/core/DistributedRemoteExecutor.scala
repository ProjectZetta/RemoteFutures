/* Copyright (c) 2014 Marvin Hansen.
 * www.marvin-hansen.tel.
 * ALl RIGHTS RESERVED
 ***************************
 * Project: DistributedRemoteFutures
 * User: Marvin Hansen
 * Web: www.marvin-hansen.tel
 * Date: 3/12/14 (/dd/mm/yy)
 * Time: 1:28 PM (CET)
 */
package org.remotefutures.core

import org.remotefutures.core.DistributionStrategy._
import org.remotefutures.network.{NodeSelector, Node}

/**
 * A distributed remote executor executes a task
 * remotely according to a certain distribution strategy
 * either on a pool of nodes, a specfic node or a sub-group
 * of nodes determined by certain properties through a node-selector.
 *
 */
//* Companion object for static access i.e. imports */
object DistributedRemoteExecutor extends DistributedRemoteExecutor

// *Composition trait, matches interface to implementation by name*/
trait DistributedRemoteExecutor extends DistributedRemoteExecutorModule with DistributedRemoteExecutorModuleImpl

// composing Interfaces together
trait DistributedRemoteExecutorModule extends DistributedRemoteExecutorComponent with RemoteExecutorComponent

// composing implementations together
trait DistributedRemoteExecutorModuleImpl extends DistributedRemoteExecutorComponentImpl with RemoteExecutorComponentImpl


//** Interface component*/
trait DistributedRemoteExecutorComponent {

  protected val distributedRemoteExecutorService: DistributedRemoteExecutorService

  protected trait DistributedRemoteExecutorService {
    // Define interface methods here
    def execute[T](runnable: PromiseCompletingRunnable[T], dist: DistributionStrategy): Unit

    def executeOnNode[T](runnable: PromiseCompletingRunnable[T], node: Node): Unit

    def executeOnNodes[T](runnable: PromiseCompletingRunnable[T], node: NodeSelector): Unit
  }

}

//*  Sample Implementation component*/
trait DistributedRemoteExecutorComponentImpl extends DistributedRemoteExecutorComponent {

  // self type (this) switches to RemoteExecutor for accessing  interface 
  self: RemoteExecutorComponent =>

  override protected val distributedRemoteExecutorService: DistributedRemoteExecutorService = new DistributedRemoteExecutorServiceImpl

  private[this] class DistributedRemoteExecutorServiceImpl extends DistributedRemoteExecutorService {
    // implement methods here
    override def execute[T](runnable: PromiseCompletingRunnable[T], dist: DistributionStrategy): Unit = {

      dist match {
        case DistributionStrategy.FIRST_WIN => remoteExecutionService.executeOnPool(runnable)
        case DistributionStrategy.LOAD_BALANCING => remoteExecutionService.executeLoadBalanced(runnable)
        case DistributionStrategy.FAIL_OVER => remoteExecutionService.executeLoadFailOver(runnable)
      }
    }

    override def executeOnNodes[T](runnable: PromiseCompletingRunnable[T], node: NodeSelector): Unit = {

    }

    override def executeOnNode[T](runnable: PromiseCompletingRunnable[T], node: Node): Unit = {
      remoteExecutionService.executeOnNode(runnable, node)
    }
  }

}