package org.remotefutures.playground

import scala.concurrent.Future
import org.remotefutures.core.RemoteFuture
import org.remotefutures.examples.Computations
import scala.util.{Success, Failure}
import org.remotefutures.spores._

object TestFuture {
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
      Computations.fibLong(x)
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

object TestFutureWithRemoteAwareExecutionContext {
  def main(args: Array[String]): Unit = {
    // import org.remotefutures.core.RemoteExecutionContextImplicits.DefaultConfigBasedRemoteExecutionContext
    // import scala.concurrent.ExecutionContext.Implicits.global

    import org.remotefutures.core.RemoteAwareExecutionContextImplicits.SimpleAkkaRemoteAwareExecutionContext

    // val xs1: List[Long] = List.fill(500)(1000000000 + (Random.nextInt(1000)))

    // val xs2 : List[Long] = List(15, 25, 17, 12, 28, 81, 324, 812, 12, 15)

    val from = 1000000000L
    val size = 100L

    val xs3: List[Long] = (from to from + size).toList

    println("Remote: " + xs3)

    val fs: List[Future[Long]] = xs3.map(x => Future {
      // println(Thread.currentThread.getName)
      Computations.fibLong(x)
    })

    val r: Future[List[Long]] = Future sequence (fs)

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


object TestRemoteFuture {
  def main(args: Array[String]): Unit = {
    import org.remotefutures.core.RemoteExecutionContextImplicits.DefaultConfigBasedRemoteExecutionContext
    import scala.concurrent.ExecutionContext.Implicits.global

    // val xs1: List[Long] = List.fill(500)(1000000000 + (Random.nextInt(1000)))

    // val xs2 : List[Long] = List(15, 25, 17, 12, 28, 81, 324, 812, 12, 15)

    val from = 1000000000L
    val size = 100L;

    val xs3: List[Long] = (from to from + size).toList

    println("Remote: " + xs3)

    val fs: List[Future[Long]] = xs3.map(x => RemoteFuture {
      // println(Thread.currentThread.getName)
      Computations.fibLong(x)
    })

    val r: Future[List[Long]] = Future sequence (fs)

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



object TestRemoteFutureWithSpores {
  def main(args: Array[String]): Unit = {
    import org.remotefutures.core.RemoteExecutionContextImplicits.DefaultConfigBasedRemoteExecutionContext
    import scala.concurrent.ExecutionContext.Implicits.global

    // val xs1: List[Long] = List.fill(500)(1000000000 + (Random.nextInt(1000)))

    // val xs2 : List[Long] = List(15, 25, 17, 12, 28, 81, 324, 812, 12, 15)

    val from = 1000000000L
    val size = 100L

    val xs3: List[Long] = (from to from + size).toList

    println("Remote: " + xs3)

    val s: Spore[Long, Long] = spore {
      (a: Long) => Computations.fibLong(a)
    }

    val fs: List[Future[Long]] = xs3.map(x => RemoteFuture {
      s(x)
    })

    val r: Future[List[Long]] = Future sequence (fs)

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

//
