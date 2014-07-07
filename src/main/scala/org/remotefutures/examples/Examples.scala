package org.remotefutures.examples

import scala.concurrent.Future
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

object RemoteFutureExample {
  def main(args: Array[String]): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global


    val tmp : ( () => Any ) = {
      () => Computations.fibLong(3)
    }

    println("tmp is " + tmp.hashCode() + "[" + tmp.getClass + "]")
    val res = tmp()
    println("tmp is " + tmp.hashCode() + "[" + tmp.getClass + "]")
    println("res is " + res.hashCode() + "[" + res.getClass + "]")


    // instead of
    //    import org.remotefutures.core.EnvironmentImplicits.DefaultConfigBasedRemoteExecutionContext
    // we use this implicit val, as to have access to the context and to be able to call startup and shutdown
    implicit val DefaultConfigBasedRemoteExecutionContext = RemoteExecutionContext.fromDefaultConfig

    DefaultConfigBasedRemoteExecutionContext.startup()

    val from = 1000000000L
    val size = 10L

    val xs3: List[Long] = (from to from + size).toList

    println("Remote: " + xs3)

    // object RemoteFuture {
    // def apply[T](body: => T)(implicit res: RemoteExecutionContext): Future[T] = impl.RemoteFutureImpl[T](body)
    // }


    val fs: List[Future[Long]] = xs3.map(x => RemoteFuture {
      // println(Thread.currentThread.getName)
      Computations.fibLong(x)
    })

    val r: Future[List[Long]] = Future sequence (fs)

    r onComplete {
      case Success(x) => {
        println(x)
        DefaultConfigBasedRemoteExecutionContext.shutdown()
      }
      case Failure(t) => {
        println("Problem " + t)
        DefaultConfigBasedRemoteExecutionContext.shutdown()
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

    DefaultConfigBasedRemoteExecutionContext.startup()

    val from = 1000000000L
    val size = 100L

    val xs3: List[Long] = (from to from + size).toList

    println("Remote: " + xs3)

    val s: Spore[Long, Long] = spore {
      (a: Long) => Computations.fibLong(a)
    }

    val fs: List[Future[Long]] = xs3.map(x => RemoteFuture {
      // println(Thread.currentThread.getName)
      s(x)
    })

    val r: Future[List[Long]] = Future sequence (fs)

    r onComplete {
      case Success(x) => {
        println(x)
        DefaultConfigBasedRemoteExecutionContext.shutdown()
      }
      case Failure(t) => {
        println("Problem " + t)
        DefaultConfigBasedRemoteExecutionContext.shutdown()
      }
    }
  }
}

object RemoteFutureWithSporesExample {
  def main(args: Array[String]): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    import org.remotefutures.core.EnvironmentImplicits.DefaultConfigBasedRemoteExecutionContext

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
