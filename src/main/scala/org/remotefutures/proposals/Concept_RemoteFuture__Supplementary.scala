/*
 * Copyright (c) 2014 Martin Senne, Marvin Hansen.
 */
package org.remotefutures.proposals

import scala.concurrent.Future
import scala.util.Try
import org.remotefutures.proposals

abstract class ExecutionLocation
case object EX_LOCAL extends ExecutionLocation
case object EX_BY_STRATEGY extends ExecutionLocation
// not good yet: case class EX_SAME_AS( rf: RemoteFuture ) extends ExecutionLocation


protected trait RemoteFuture[+T] {

  def map[S]( f: T => S )(implicit location: ExecutionLocation) : RemoteFuture[S]

  def flatMap[S](f: T => RemoteFuture[S])(implicit location: ExecutionLocation): RemoteFuture[S]

  /**
   * When this remote future is completed on caller site, either through an exception, or a value,
   * apply the provided function.
   *
   * (executor: RemoteExecutionContext) was changed to (location: ExecutionLocation)
   */
  def onComplete[U]( f: Try[T] => U)(implicit location: ExecutionLocation): Unit

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
  override def onComplete[U](f: (Try[T]) => U)(implicit  location: ExecutionLocation): Unit = ???

  override def flatMap[S](f: (T) => RemoteFuture[S])(implicit location: ExecutionLocation): RemoteFuture[S] = ???

  override def map[S](f: (T) => S)(implicit location: ExecutionLocation): RemoteFuture[S] = ???
}


object RemoteFuture {

  def apply[T]( f: => T )( implicit location: ExecutionLocation) : RemoteFuture[T] = {
    new RemoteFutureImpl[T]
  }

  // Alternative idea: keep the location unset, meaning that
  // RemoteFuture { } returns an UnlocalizedRemoteFuture
  //   def RemoteFuture( ... ) : UnlocalizedRemoteFuture
  // An UnlocalizedRemoteFuture is turned in an RemoteFuture by a location.
  //
  // trait UnlocalizedRemoteFuture {
  //   apply(location: ExecutionLocation) : RemoteFuture
  // }
}


object ConceptSupplementary {

  type UnlocalizedRemoteFuture[U] = (ExecutionLocation => RemoteFuture[U])

  def heavyComputationOfFirstFactor = 42

  def heavyComputationOfSecondFactor = 80

  def main(args: Array[String]) : Unit = {
    val loc1 = EX_BY_STRATEGY
    implicit val standardLocation = EX_LOCAL



    val f1 : RemoteFuture[Int] = RemoteFuture{ heavyComputationOfFirstFactor }(loc1)
    val f2 : RemoteFuture[Int] = RemoteFuture { heavyComputationOfSecondFactor }
    val f3 : UnlocalizedRemoteFuture[Int] = {
      case EX_LOCAL => {
        RemoteFuture { 32 }
      }
      case _ => {
        RemoteFuture { 12 }
      }
    }

    val fMultiplication: RemoteFuture[Int] = {
      for {
        v1 <- f1
        v2 <- (f3(loc1))
      } yield v1*v2
    }
  }
}


// =====================================================================================================================
// =====================================================================================================================
// =====================================================================================================================
// =====================================================================================================================
// =====================================================================================================================

class UnlocRemoteFuture[+T](f : () => T) {
// extends Function[ExecutionLocation, RemoteFuture[T]] {

//  def apply( location: ExecutionLocation ) : RemoteFuture[T] = {
//    RemoteFuture( f() )( location )
//  }

  def at( location: ExecutionLocation ) : RemoteFuture[T] = {
    RemoteFuture( f() )( location )
  }

  def map[S]( f: T => S )( location: ExecutionLocation ) : UnlocRemoteFuture[S] = ???

  def flatMap[S](f: T => UnlocRemoteFuture[S])( location: ExecutionLocation ): UnlocRemoteFuture[S] = ???

  /**
   * When this remote future is completed on caller site, either through an exception, or a value,
   * apply the provided function.
   *
   * (executor: RemoteExecutionContext) was changed to (location: ExecutionLocation)
   */
  def onComplete[U]( f: Try[T] => U): Unit = ???
}


object UnlocRemoteFuture {

  def apply[T](f: => T) : UnlocRemoteFuture[T] = {
    new UnlocRemoteFuture[T]( () => f )
  }

}
//
//object Tmp {
//  def strlen = {
//    x:String => x.length
//  }
//
//  def doit : (String => Int) = {
//    x => x.length - 3
//  }
//
//  val res = for {
//    a <- strlen
//  } yield a
//
//}

object ConceptSupplementary__ {

  def heavyComputationOfFirstFactor = 42

  def heavyComputationOfSecondFactor = 80

  def main(args: Array[String]) : Unit = {
    val t1: UnlocRemoteFuture[Int] = UnlocRemoteFuture {
      heavyComputationOfFirstFactor
    }
    val t2 = UnlocRemoteFuture {
      heavyComputationOfSecondFactor
    }

    // simple with map
    val r1: UnlocRemoteFuture[Int] = t1.map(v1 => v1*5)(EX_LOCAL)

    // explicit
//    val r2: (ExecutionLocation => UnlocRemoteFuture[Int]) = for {
//      v1 <- t1
//    } yield v1*5
//    val r2_ = r2(EX_LOCAL)

    val r3: UnlocRemoteFuture[Int] = (for {
      v1 <- t1
    } yield v1*5)(EX_LOCAL)


    // execute flatmap v1 => v1 * _    by strategy
    // execute map v2 => v1 *v1        locally
    val s1 : UnlocRemoteFuture[Int] = t1.flatMap(v1 => t2.map( v2 => v1*v2 )(EX_LOCAL))(EX_BY_STRATEGY)


    // HOW does it look like with for-comprehension???
//    val s2 : UnlocRemoteFuture[Int] = for {
//      v1 <- t1
//      v2 <- t2
//    } yield v1*v2



    val loc1 = EX_BY_STRATEGY
    implicit val standardLocation = EX_LOCAL

    val u1 : UnlocRemoteFuture[Int] = UnlocRemoteFuture { heavyComputationOfFirstFactor }
    val u2 : UnlocRemoteFuture[Int] = UnlocRemoteFuture { heavyComputationOfSecondFactor }

    val fMultiplication: RemoteFuture[Int] = {
      for {
        v1 <- u1 at EX_BY_STRATEGY
        v2 <- u2 at EX_LOCAL
      } yield v1*v2
    }
  }
}

