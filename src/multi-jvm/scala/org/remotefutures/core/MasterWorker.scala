package org.remotefutures.core

import java.net.InetAddress

import akka.actor.Address
import org.remotefutures.core.impl.akka.pullingworker.controllers.FrontendController
import org.remotefutures.core.NodeControllers


/**
 * Start the master worker example with "multi-jvm:run org.remotefutures.core.MasterWorker"
 *
 * The master worker example consists of three nodes:
 * - master
 * - worker
 * - frontend
 */

object MasterWorkerMultiJvmNode1 {
  def main(args: Array[String]) {
    val controllers = NodeControllers.fromDefaultConfig
    val frontEndController  = controllers("frontend")
    frontEndController match {
      case Some(x) => x.start(123)
      case None => throw new Exception("Can not start frontend node. Aborting")
    }



    // val joinAddress = startMaster(None, "backend")
    // println("Master has default address: " + InetAddress.getLocalHost.getHostAddress );
    // startMaster(Some(joinAddress), "backend")
  }
}

object MasterWorkerMultiJvmNode2 {
  def main(args: Array[String]) {
    val controllers = NodeControllers.fromDefaultConfig
    val masterController = controllers("master")
    masterController match {
      case Some(x) => x.start(234)
      case None => throw new Exception("Can not start master node. Aborting")
    }
  }
}

//object MasterWorkerMultiJvmNode3 {
//  def main(args: Array[String]) {
//    println("Hello from node 3")
//  }
//}

// implicit val rec = org.remotefutures.core.RemoteExecutionContextImplicits.defaultConfigBasedRemoteExecutionContext