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
      FibonacciComputations.fibLong(x)
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
        DefaultConfigBasedRemoteExecutionContext.shutdown()
      }
      case Failure(t) => {
        println("Problem " + t)
        DefaultConfigBasedRemoteExecutionContext.shutdown()
      }
    }
  }
}

// TODO: Remove this example, as it uses spores incorrectly
// Especially, the spore is created and used, without binding the input variable
object RemoteFutureWithSporesExample__NotWorking {
  def main(args: Array[String]): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    import org.remotefutures.core.EnvironmentImplicits.DefaultConfigBasedRemoteExecutionContext

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
        DefaultConfigBasedRemoteExecutionContext.shutdown()
      }
      case Failure(t) => {
        println("Problem " + t)
        DefaultConfigBasedRemoteExecutionContext.shutdown()
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
    import scala.concurrent.ExecutionContext.Implicits.global
    import org.remotefutures.core.EnvironmentImplicits.DefaultConfigBasedRemoteExecutionContext

    // val xs1: List[Long] = List.fill(500)(1000000000 + (Random.nextInt(1000)))

    // val xs2 : List[Long] = List(15, 25, 17, 12, 28, 81, 324, 812, 12, 15)

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
        DefaultConfigBasedRemoteExecutionContext.shutdown()
      }
      case Failure(t) => {
        println("Problem " + t)
        DefaultConfigBasedRemoteExecutionContext.shutdown()
      }
    }
  }
}

//

//}