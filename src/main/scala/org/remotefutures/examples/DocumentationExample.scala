package org.remotefutures.examples

import scala.concurrent.Future
import scala.util.Try


case class ExecutionLocation( location: String )

trait RemoteFuture[+T] {

  def map[S]( f: T => S )(location: ExecutionLocation) : RemoteFuture[S]

  def flatMap[S](f: T => RemoteFuture[S])(location: ExecutionLocation): RemoteFuture[S]

  /**
   * When this remote future is completed on caller site, either through an exception, or a value,
   * apply the provided function.
   *
   * (executor: RemoteExecutionContext) was changed to (location: ExecutionLocation)
   */
  def onComplete[U]( f: Try[T] => U)(location: ExecutionLocation): Unit

  /**
   * Turn this RemoteFuture[T] into a Future[T]
   */
  def makeLocal : Future[T]
}

class RemoteFutureImpl[+T]
extends RemoteFuture[T] {
  /**
   * Turn this RemoteFuture[T] into a Future[T]
   */
  override def makeLocal: Future[T] = ???

  /**
   * When this remote future is completed on caller site, either through an exception, or a value,
   * apply the provided function.
   *
   * (executor: RemoteExecutionContext) was changed to (location: ExecutionLocation)
   */
  override def onComplete[U](f: (Try[T]) => U)(location: ExecutionLocation): Unit = ???

  override def flatMap[S](f: (T) => RemoteFuture[S])(location: ExecutionLocation): RemoteFuture[S] = ???

  override def map[S](f: (T) => S)(location: ExecutionLocation): RemoteFuture[S] = ???
}


// =========================================

object RemoteFuture {
  def apply[T]( f: => T ) : RemoteFuture[T] = {
    new RemoteFutureImpl[T]
  }
}


object DocumentationExample {

  def heavyComputationOfFirstFactor = 42

  def heavyComputationOfSecondFactor = 80

  def main(args: Array[String]) : Unit = {
    val f1 : RemoteFuture[Int] = RemoteFuture { heavyComputationOfFirstFactor }
    val f2 : RemoteFuture[Int] = RemoteFuture { heavyComputationOfSecondFactor }
    val fMultiplication: RemoteFuture[Int] = {
      for {
        v1 <- f1
        v2 <- f2
      } yield v1*v2
    }
  }
}
