package org.remotefutures.playground

import scala.concurrent.Future
import org.remotefutures.core.RemoteFuture
import org.remotefutures.examples.Computations
import scala.util.{Success, Failure}
import org.remotefutures.spores._


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

    // val xs1: List[Long] = List.fill(500)(1000000000 + (Random.nextInt(1000)))

    // val xs2 : List[Long] = List(15, 25, 17, 12, 28, 81, 324, 812, 12, 15)

    val from = 1000000000L
    val size = 100L;

    val xs3 : List[Long] = (from to from+size).toList

    println("Future: " + xs3)

    val fs: List[Future[Long]] = xs3.map( x => Future {
      // println(Thread.currentThread.getName)
      Computations.fibLong(x)
    } )

    val r: Future[List[Long]] = Future sequence( fs )

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
    import org.remotefutures.core.EnvironmentImplicits.DefaultConfigBasedRemoteExecutionContext
    import scala.concurrent.ExecutionContext.Implicits.global

    // val xs1: List[Long] = List.fill(500)(1000000000 + (Random.nextInt(1000)))

    // val xs2 : List[Long] = List(15, 25, 17, 12, 28, 81, 324, 812, 12, 15)

    val from = 1000000000L
    val size = 100L;

    val xs3 : List[Long] = (from to from+size).toList

    println("Remote: " + xs3)

    val fs: List[Future[Long]] = xs3.map( x => RemoteFuture {
      // println(Thread.currentThread.getName)
      Computations.fibLong(x)
    } )

    val r: Future[List[Long]] = Future sequence( fs )

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

object TestWithRemoteAndSpores {
  def main(args:Array[String]): Unit = {
    import org.remotefutures.core.EnvironmentImplicits.DefaultConfigBasedRemoteExecutionContext
    import scala.concurrent.ExecutionContext.Implicits.global

    // val xs1: List[Long] = List.fill(500)(1000000000 + (Random.nextInt(1000)))

    // val xs2 : List[Long] = List(15, 25, 17, 12, 28, 81, 324, 812, 12, 15)

    val from = 1000000000L
    val size = 100L

    val xs3 : List[Long] = (from to from+size).toList

    println("Remote: " + xs3)

    val s: Spore[Long, Long] = spore {
      (a:Long) => Computations.fibLong(a)
    }

    val fs: List[Future[Long]] = xs3.map( x => RemoteFuture {
      s(x)
    } )

    val r: Future[List[Long]] = Future sequence( fs )

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

//
