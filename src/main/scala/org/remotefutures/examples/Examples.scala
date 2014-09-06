package org.remotefutures.examples

import org.remotefutures.core._
import org.remotefutures.core.impl.akka.pullingworker.controllers.{FrontEndNodeType, FrontEndInformation}


import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Success, Failure}
import org.remotefutures.spores._
import org.remotefutures.core.{RemoteExecutionContext, RemoteFuture}

object FutureExample {
  def main(args: Array[String]): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global

    // val xs1: List[Long] = List.fill(500)(1000000000 + (Random.nextInt(1000)))

    // val xs2 : List[Long] = List(15, 25, 17, 12, 28, 81, 324, 812, 12, 15)

    val from = 1000000000L
    val size = 100L

    val xs3: List[Long] = (from to from + size).toList

    println("Future: " + xs3)

    val fs: List[Future[Long]] = xs3.map(x => Future {
      // println(Thread.currentThread.getName)
      FibonacciComputations.fibLong(x)
    })

    val r: Future[List[Long]] = Future sequence fs

    r onComplete {
      case Success(x) => {
        println(x)
      }
      case Failure(t) => {
        println("Problem " + t)
      }
    }
  }
}

object RemoteFutureExample {
  def main(args: Array[String]): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global


    val tmp : ( () => Any ) = {
      () => FibonacciComputations.fibLong(3)
    }

    println("tmp is " + tmp.hashCode() + "[" + tmp.getClass + "]")
    val res = tmp()
    println("tmp is " + tmp.hashCode() + "[" + tmp.getClass + "]")
    println("res is " + res.hashCode() + "[" + res.getClass + "]")


    // instead of
    //    import org.remotefutures.core.EnvironmentImplicits.DefaultConfigBasedRemoteExecutionContext
    // we use this implicit val, as to have access to the context and to be able to call startup and shutdown
    implicit val DefaultConfigBasedRemoteExecutionContext = RemoteExecutionContext.fromDefaultConfig

    // DefaultConfigBasedRemoteExecutionContext.nodeControllers.getNodeController()

    val from = 1000000000L
    val size = 10L

    val xs3: List[Long] = (from to from + size).toList

    println("Remote: " + xs3)

    // object RemoteFuture {
    // def apply[T](body: => T)(implicit res: RemoteExecutionContext): Future[T] = impl.RemoteFutureImpl[T](body)
    // }


    val fs: List[Future[Long]] = xs3.map(x => RemoteFuture {
      // println(Thread.currentThread.getName)
      FibonacciComputations.fibLong(x)
    })

    val r: Future[List[Long]] = Future sequence (fs)

    r onComplete {
      case Success(x) => {
        println(x)
        // DefaultConfigBasedRemoteExecutionContext.shutdown()
      }
      case Failure(t) => {
        println("Problem " + t)
        // DefaultConfigBasedRemoteExecutionContext.shutdown()
      }
    }
  }
}

object RemoteFutureExample2 {
  def main(args: Array[String]): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global

    // instead of
    //    import org.remotefutures.core.EnvironmentImplicits.DefaultConfigBasedRemoteExecutionContext
    // we use this implicit val, as to have access to the context and to be able to call startup and shutdown
    implicit val DefaultConfigBasedRemoteExecutionContext = RemoteExecutionContext.fromDefaultConfig

    // DefaultConfigBasedRemoteExecutionContext.getNodeController()

    val from = 1000000000L
    val size = 10L

    val xs3: List[Long] = (from to from + size).toList

    println("Remote: " + xs3)

    val s: Spore[Long, Long] = spore {
      (a: Long) => FibonacciComputations.fibLong(a)
    }

    val fs: List[Future[Long]] = xs3.map(x => RemoteFuture {
      // println(Thread.currentThread.getName)
      s(x)
    })

    val r: Future[List[Long]] = Future sequence (fs)

    r onComplete {
      case Success(x) => {
        println(x)
        // DefaultConfigBasedRemoteExecutionContext.shutdown()
      }
      case Failure(t) => {
        println("Problem " + t)
        // DefaultConfigBasedRemoteExecutionContext.shutdown()
      }
    }
  }
}

// TODO: Remove this example, as it uses spores incorrectly
// Especially, the spore is created and used, without binding the input variable
object RemoteFutureWithSporesExample__NotWorking {
  def main(args: Array[String]): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    implicit val configBasedRemoteExecutionContext = org.remotefutures.core.RemoteExecutionContextImplicits.defaultConfigBasedRemoteExecutionContext

    // val xs1: List[Long] = List.fill(500)(1000000000 + (Random.nextInt(1000)))

    // val xs2 : List[Long] = List(15, 25, 17, 12, 28, 81, 324, 812, 12, 15)

    println("===========================")
    println("= DO NOT USE THIS EXAMPLE =")
    println("===========================")

    val from = 1000000000L
    val size = 10L

    val xs3: List[Long] = (from to from + size).toList

    println("Remote: " + xs3)

    val s: Spore[Long, Long] = spore {
      (a: Long) => FibonacciComputations.fibLong(a)
    }

    val fs: List[Future[Long]] = xs3.map(x => RemoteFuture {
      s(x)
    })

    val r: Future[List[Long]] = Future sequence (fs)

    r onComplete {
      case Success(x) => {
        println(x)
        // DefaultConfigBasedRemoteExecutionContext.shutdown()
      }
      case Failure(t) => {
        println("Problem " + t)
        // DefaultConfigBasedRemoteExecutionContext.shutdown()
      }
    }
  }
}


/**
* Working example using spores and pulling worker (recheck remotefutures.conf to see if correct implementation is chosen)
*
* TODO: Add syntax improvement.
*
* This includes expecially:
   With a few simple changes, the following is possible
       RemoteFuture(<variables_of_closure_to_be_captured){ <body> }

     e.g.
       RemoteFuture( val r = x ) {
        Computations.fibLong(r)
       }

     Spores are used to check, if only stable paths are used and only val elements are accessed from within body.

     Signature and possible implementation of RemoteFuture.apply :

       RemoteFuture.apply[T]( context: => Unit )( body: => T ) : Future[T] = {
         // create a compiler macro (or adept the spore macro

         // use the check method  in org.remotefutures.spores.package
       }

*/
object RemoteFutureWithSporesExample_Working {
  def main(args: Array[String]): Unit = {
    import scala.concurrent.duration._
    import scala.concurrent.ExecutionContext.Implicits.global

    implicit val configBasedRemoteExecutionContext = org.remotefutures.core.RemoteExecutionContextImplicits.defaultConfigBasedRemoteExecutionContext
    val nodeTypes = configBasedRemoteExecutionContext.nodeControllers.nodeTypes

    println(nodeTypes)

    val from = 1000000000L
    val size = 10L

    val xs3: List[Long] = (from to from + size).toList

    println("Remote: " + xs3)


    // uses new apply method in RemoteFuture
    //   def apply[T](spore: NullarySpore[T])(implicit res: RemoteExecutionContext): Future[T] = impl.RemoteFutureImpl[T](spore)
    val fs: List[Future[BigInt]] = xs3.map(x => RemoteFuture {
      spore {
        val r = x
        () => {
          FibonacciComputations.fibBigInt(r)
        }
      }
    })

    val r: Future[List[BigInt]] = Future sequence (fs)

    r onComplete {
      case Success(x) => {
        println(x)
      }
      case Failure(t) => {
        println("Problem " + t)

      }
    }

    Await.result(r, 10.seconds)
    println("Shutting down due to timeout.")
    // DefaultConfigBasedRemoteExecutionContext.shutdown()
  }
}





//object RunIt {
//
//
//  def main(args: Array[String]) : Unit = {
//
//
//
//    def start( port:Int ) : State[NodeState, NodeInformation[FrontEndNodeType.type]] = {
//      State {
//        s => s match {
//          case NodeDown => {
//            println("This is frontend. Starting frontend node.")
//            (NodeUp, FrontEndInformation(null, null))
//          }
//          case NodeUp => {
//            println("Node is already up.")
//            (NodeUp, FrontEndInformation(null, null))
//          }
//        }
//      }
//    }
//
//    def stop() : State[NodeState, NodeInformation[FrontEndNodeType.type]] = {
//      State {
//        s => s match {
//          case NodeDown => {
//            println("This is frontend. Trying to stop an already stopped frontend node.")
//            (NodeDown, FrontEndInformation(null, null))
//          }
//          case NodeUp => {
//            println("This is frontend. Stopping frontend node.")
//            (NodeDown, FrontEndInformation(null, null))
//          }
//        }
//      }
//    }
//
//    val node: State[NodeState, NodeInformation[FrontEndNodeType.type]] = start(3)
//    // val node_s2: State[NodeState, Int] = node.map( ni => 3 )
//    val node_s3 = node.flatMap( (a: NodeInformation[FrontEndNodeType.type]) => stop )
//
//
//    val r = node_s3.run(NodeDown)
//    println("result: " + r)
//
//    println("=====")
//
//    val x: State[NodeState, NodeInformation[FrontEndNodeType.type]] = for {
//      r <- start(3)
//      s <- stop()
//    } yield s
//
//    val forResult: (NodeState, NodeInformation[FrontEndNodeType.type]) = x.run( NodeDown )
//    println(forResult)
//
//
//
//    val s = State[String, Int] {
//      s => ( s + "a", s.length)
//    }
//    val sa2: (String, Int) = s.run("start")
//    println("sa2: " + sa2)
//
//    val s2: State[String, Double] = s.map( x => x*2.1)
//    val sa3: (String, Double) = s2.run("run")
//    println("sa3: " + sa3)
//
//    println(s)
//  }
//}
