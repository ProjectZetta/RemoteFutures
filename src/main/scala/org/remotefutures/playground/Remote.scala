package org.remotefutures.playground

import scala.concurrent.{Promise, Future, ExecutionContext}
import org.remotefutures.core.{RemoteFuture, RemoteExecutionContext}
import org.remotefutures.examples.RemoteExampleFibonacci
import scala.util.{Random, Success, Failure}


//trait Remote[+T, C] {
//  def execute[C, T](fnc: () => T, fncContext: C)
//
//  def map[S, D](f: T => S)(implicit executor: RemoteExecutionContext): Remote[S, D] = { // transform(f, identity)
//
//
//
////    val p = Promise[S]()
////    onComplete { v => p complete (v map f) }
////    p.future
//    Remote {
//      new Remote(f)
//    }
//  }
//}
//
//class RemoteImpl[+T, C] extends Remote {
//  override def execute[C, T](fnc: () => T, fncContext: C): Unit = ???
//}
//
//object Remote {
//  def apply[T, C]( x: => T ) = {
//    new RemoteImpl
//  }
//}



object TestWithFuture {
  def main(args:Array[String]): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global

    val xs1: List[Int] = List.fill(500)(1000000000 + (Random.nextInt(1000)))

    val xs2 : List[Int] = List(15, 25, 17, 12, 28, 81, 324, 812, 12, 15)

    val xs3 : List[Int] = (1 to 5000).toList

    val fs: List[Future[Int]] = xs3.map( x => Future {
      // println(Thread.currentThread.getName)
      RemoteExampleFibonacci.fib(x)
    } )

    val r: Future[List[Int]] = Future sequence( fs )

    r onComplete {
      case Success(x) => {
        println(x)
      }
      case Failure(t) => {
        println("Problem " + t )
      }
    }
  }
}

object TestWithRemote {
  def main(args:Array[String]): Unit = {
    import org.remotefutures.core.EnvironmentImplicits.ConfigFileBaseRemoteExecutionContext
    import scala.concurrent.ExecutionContext.Implicits.global

    val xs1: List[Int] = List.fill(500)(1000000000 + (Random.nextInt(1000)))

    val xs2 : List[Int] = List(15, 25, 17, 12, 28, 81, 324, 812, 12, 15)

    val xs3 : List[Int] = (1 to 20).toList

    val fs: List[Future[Int]] = xs3.map( x => RemoteFuture {
      // println(Thread.currentThread.getName)
      RemoteExampleFibonacci.fib(x)
    } )

    val r: Future[List[Int]] = Future sequence( fs )

    r onComplete {
      case Success(x) => {
        println(x)
      }
      case Failure(t) => {
        println("Problem " + t )
      }
    }
  }
}
