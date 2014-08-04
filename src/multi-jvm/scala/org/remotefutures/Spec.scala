package org.remotefutures

import org.scalatest.WordSpec
import org.scalatest.MustMatchers

/**
 * Naming scheme: {TestName}MultiJvm{NodeName}
 *
 * Call with "multi-jvm:test-only org.remotefutures.Spec"
 *
 * http://doc.akka.io/docs/akka/snapshot/dev/multi-jvm-testing.html
 */
class SpecMultiJvmNode1 extends WordSpec with MustMatchers {
  println("Node 1")
  "A node" should {
    "be able to say hello" in {
      val message = "Hello from node 1"
      message must be("Hello from node 1")
    }
  }
}

class SpecMultiJvmNode2 extends WordSpec with MustMatchers {
  "A node" should {
    "be able to say hello" in {
      val message = "Hello from node 2"
      message must be("Hello from node 2")
    }
  }
}