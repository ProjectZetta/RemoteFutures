package org.remotefutures.proposals

/*
 * Copyright (c) 2014 Martin Senne, Marvin Hansen.
 */
package org.remotefutures.proposals

import scala.concurrent.{Promise, Await, ExecutionContext, Future}
import scala.util.Try
import scala.util.Success
import scala.util.Failure
import java.util.concurrent.Executor
import scala.util.control.NonFatal

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
}

class RemoteFutureImpl[+T]
extends RemoteFuture[T] {

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
}


object ConceptSupplementary {

  type UnlocalizedRemoteFuture[U] = (ExecutionLocation => RemoteFuture[U])

  def heavyComputationOfFirstFactor = 42

  def heavyComputationOfSecondFactor = 80

  def main(args: Array[String]) : Unit = {
    val loc1 = EX_BY_STRATEGY
    implicit val standardLocation = EX_LOCAL



    val f1 : RemoteFuture[Int] = RemoteFuture { heavyComputationOfFirstFactor }(loc1)
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
    val s1a : UnlocRemoteFuture[Int] = t1.flatMap(v1 => (for ( v2 <- t2 ) yield v1*v2)(EX_LOCAL))(EX_BY_STRATEGY)
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


object Fun {
  // import scala.concurrent.ExecutionContext.Implicits.global

  implicit val ex1 = scala.concurrent.ExecutionContext.Implicits.global
  implicit val standardLocation = EX_LOCAL

  def computationA: String = {
    "asdf" + "ibjmaretg"
  }

  def computationB: Int = {
    (25 * 342 + 12) * 2
  }

  def computationC(x: String, y: Int): Double = {
    (x.length + y) * 0.3
  }

  // Scenarios:
  //
  // Variant | A           | B           | C           |
  // ---------------------------------------------------
  //       1 | local       | local       | local       |
  //       2 | remote      | remote      | local       |
  //       3 | remote      | remote      | remote      |
  //       4 | local       | local       | remote      |

  // =============================================================
  // variant 1
  val fx1 = Future(computationA)
  val fy1 = Future(computationB)

  val z1: Future[Double] = for {
    x1 <- fx1
    y1 <- fy1
  } yield computationC(x1, y1)

  // =============================================================
  // variant 2
  val rfx2 = RemoteFuture(computationA)
  val rfy2 = RemoteFuture(computationB)

  val rfz2 = for {
    x1 <- fx1
    y1 <- fy1
  } yield computationC(x1, y1)

  // =============================================================
  // variant 4
  val fx4 = Future(computationA)
  val fy4 = Future(computationB)

  val rfz4 = Remote {
    for {
      x1 <- fx4
      y1 <- fy4
    } yield computationC(x1, y1)
  }
}

object RealFun {
  def computationA: String = {
    "asdf" + "ibjmaretg"
  }

  def computationB: Int = {
    (25 * 342 + 12) * 2
  }

  def computationC(x: String, y: Int): Double = {
    (x.length + y) * 0.3
  }

  def main2(args: Array[String]) : Unit = {

    implicit val defEc = ExecutionContext.Implicits.global

    val ec1 = ExecutionContext.Implicits.global
    val ex2 = ExecutionContextImpl.fromExecutor(2, null: Executor)

    val fx5 = Future(computationA)(ec1)
    val fy5 = Future(computationB)(ec1)

    val fz5 = (for {
      x1 <- fx5
      y1 <- fy5
    } yield computationC(x1, y1))(ex2)

    // val fz5 = fx5.flatMap(x1 => fy5.map( y1 => computationC(x1, y1) )(defEc))(ex2)

  }

  def main(args: Array[String]) : Unit = {
    // implicit val defEc = ExecutionContext.Implicits.global
    implicit var defEc = ExecutionContextImpl.fromExecutor(0, null: Executor)

    // val ec1 = ExecutionContext.Implicits.global
    val ec1 = ExecutionContextImpl.fromExecutor(1, null: Executor)
    val ec2 = ExecutionContextImpl.fromExecutor(2, null: Executor)

    val fx5 = Future(computationA)(ec1)
    val fy5 = Future(computationB)(ec1)

    val fz5 = (for {
      x1 <- fx5
      if { defEc = ec2; true }

      // implicit val ex = ec2;
      // ex = ec2
      y1 <- fy5
    } yield computationC(x1, y1))(ec2)

    // val fz5 = fx5.flatMap(x1 => fy5.map( y1 => computationC(x1, y1) )(defEc))(ex2)

  }
}

object RealFun2 {
  import ExecutionContext.Implicits.global
  import scala.async.Async.{async, await}
  import scala.concurrent.duration._
  import scala.concurrent.duration.Duration.Inf

  def slowCalcFuture: Future[Int] = Future {
    23 + 23 + 23 + 23
  }

//  def main(args: Array[String]) : Unit = {
//
//    def combined: Future[Int] = async {
//      // 02
//      await(slowCalcFuture) + await(slowCalcFuture) // 03
//    }
//    val x: Int = Await.result(combined, 10.seconds) // 05
//  }

  def main(args: Array[String]) : Unit = {

    def combined: Future[Int] = async {
      val f1 = slowCalcFuture
      val f2 = slowCalcFuture
      await(f1) + await(f2) // 03
    }
    val x: Int = Await.result(combined, 10.seconds) // 05
  }

  class StateMachine extends AnyRef with (Try[Any] => Unit) with (() => Unit) {
    // class stateMachine$macro$1 extends AnyRef with scala.util.Try[Any] => Unit with () => Unit {
    var await$macro$3$macro$7: Int = 0;
    var await$macro$5$macro$8: Int = 0;
    var state: Int = 0;
    val result: scala.concurrent.Promise[Int] = Promise.apply[Int]();

    val execContext: scala.concurrent.ExecutionContextExecutor = scala.concurrent.ExecutionContext.Implicits.global;

    def resume(): Unit = try {
      state match {
        case 0 => {
          ();
          val awaitable2: Future[Int]@scala.reflect.internal.annotations.uncheckedBounds = RealFun2.this.slowCalcFuture;
          awaitable2.onComplete[Unit](this)(execContext);
          ()
        }
        case 1 => {
          val awaitable4: Future[Int]@scala.reflect.internal.annotations.uncheckedBounds = RealFun2.this.slowCalcFuture;
          awaitable4.onComplete[Unit](this)(execContext);
          ()
        }
        case 2 => {
          result.complete(Success.apply[Int]({
            val x$macro$6: Int = await$macro$5$macro$8;
            await$macro$3$macro$7.+(x$macro$6)
          }));
          ()
        }
      }
    } catch {
      case (throwable@_) if NonFatal.apply(throwable) => {
        result.complete(Failure.apply[Int](throwable));
        ()
      }
    };

    def apply(tr: scala.util.Try[Any]): Unit = state match {
      case 0 => {
        if (tr.isFailure) {
          result.complete(tr.asInstanceOf[scala.util.Try[Int]]);
          ()
        }
        else {
          await$macro$3$macro$7 = tr.get.asInstanceOf[Int];
          state = 1;
          resume()
        };
        ()
      }
      case 1 => {
        if (tr.isFailure) {
          result.complete(tr.asInstanceOf[scala.util.Try[Int]]);
          ()
        }
        else {
          await$macro$5$macro$8 = tr.get.asInstanceOf[Int];
          state = 2;
          resume()
        };
        ()
      }
    };

    def apply: Unit = resume();

    val extra: Unit = ();
  };

  def foo :Unit = {
    val stateMachine = new StateMachine();
    Future.apply (stateMachine.apply) (stateMachine.execContext);
    stateMachine.result.future
  }

}




object Remote {
  def apply[T](f: => Future[T]) : Future[T] = {
    val r = new Remote[T]( () => f )
    r.future
  }
}

class Remote[+T](f : () => Future[T]) {
  def future: Future[T] = ???
}

// def async[T](body: => T): Future[T]
// def await[T](future: Future[T]): T



