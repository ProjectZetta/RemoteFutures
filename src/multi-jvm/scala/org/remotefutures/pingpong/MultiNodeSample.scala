package org.remotefutures.pingpong

import akka.remote.testkit.MultiNodeSpec
import akka.testkit.ImplicitSender
import akka.actor.{Props, Actor}

// start with "multi-jvm:test-only org.remotefutures.pingpong.MultiNodeSampleSpec"
//
//class MultiNodeSampleSpecMultiJvmNode1 extends MultiNodeSample
//class MultiNodeSampleSpecMultiJvmNode2 extends MultiNodeSample
//
//object MultiNodeSample {
//  class Ponger extends Actor {
//    def receive = {
//      case "ping" => sender() ! "pong"
//    }
//  }
//}
//
//class MultiNodeSample extends MultiNodeSpec(MultiNodeSampleConfig)
//with STMultiNodeSpec with ImplicitSender {
//
//  import MultiNodeSampleConfig._
//  import MultiNodeSample._
//
//  def initialParticipants = roles.size
//
//  "A MultiNodeSample" must {
//
//    "wait for all nodes to enter a barrier" in {
//      enterBarrier("startup")
//    }
//
//    "send to and receive from a remote node" in {
//      runOn(node1) {
//        enterBarrier("deployed")
//        println("Sending ping to ponger")
//
//        val ponger = system.actorSelection(node(node2) / "user" / "ponger")
//        ponger ! "ping"
//        expectMsg("pong")
//      }
//
//      runOn(node2) {
//        system.actorOf(Props[Ponger], "ponger")
//        enterBarrier("deployed")
//      }
//
//      enterBarrier("finished")
//    }
//  }
//}