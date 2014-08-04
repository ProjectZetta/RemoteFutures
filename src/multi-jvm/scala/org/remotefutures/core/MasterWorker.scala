package org.remotefutures.core


/**
 * Start the master worker example with "multi-jvm:run org.remotefutures.MasterWorker"
 */

object MasterWorkerMultiJvmNode1 {
  def main(args: Array[String]) {
    println("Hello from node 1")
  }
}

object MasterWorkerMultiJvmNode2 {
  def main(args: Array[String]) {
    println("Hello from node ss2")
  }
}

object MasterWorkerMultiJvmNode3 {
  def main(args: Array[String]) {
    println("Hello from node 3")
  }
}