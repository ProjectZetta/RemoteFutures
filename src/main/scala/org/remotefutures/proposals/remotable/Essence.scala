package org.remotefutures.proposals.essence

import org.remotefutures.core.{impl, RemoteExecutionContext}
import org.remotefutures.spores._

import scala.concurrent.{ExecutionContext, Promise, Future}
import scala.util.{Success, Failure}

/**
 * Created by mse on 12.11.14.
 */
object Remote {

  // () => Future[T] ......
  // () => Spore[Future[T]] ....
  // Spore[Future[T]] ...... () (with context) => Future[T]

  def apply[T](r: NullarySpore[Future[T]]) : Future[T] = {
    println("Before remote apply - could be transfer")
    r.apply()
  }

  def apply[R, T](r: Spore[R, Future[T]]) : Future[T] = {
    null
  }

//  def apply[T]( r: => Future[T]) : Future[T] = {
//    null
//  }
}

object A {
  def foo(a:Int, b:Int):Int = {
    a * b * 3
  }
}

object Essence {

//  import scala.concurrent.ExecutionContext.Implicits.global
//
//  def bar1(): Unit = {
//    val x = Future("asdf")
//    val y = x.map(x => x.length) // executed locally
//  }
//
//  def r(y: Int) : (String => Int ) = {
//    x => x.length * y
//  }
//
//  def bar_first(): Unit = {
//    val x: Future[Int] = Remote {
//      spore {
//        () => {
//            println("first __ inside")
//            Future {
//              "asdf"
//            }
//        }
//      }
//    }.map(x => x.length)
//
//    x.onComplete {
//      case Success(x) => { println(x)}
//      case Failure(x) => { println("error in future")}
//    }
//  }
//
//
//  def bar_second(): Unit = {
//    val x: Future[Int] = Remote {
//      spore {
//        () => {
//          Future {
//            println("second __ inside")
//            "asdf"
//          }.map( x => capture(x).length )
//        }
//      }
//    }
//
//    x.onComplete {
//      case Success(x) => { println(x)}
//      case Failure(x) => { println("error in future")}
//    }
//  }
//
//  def bar__collection(): Unit = {
//    val es: List[String] = List("a", "b", "c")
//
//    val fus: List[Future[Int]] = es.map( e => Remote {
//      spore{ () => Future {e.length} }
//    })
//  }
//
//  def main(args:Array[String]) : Unit = {
//    println("First")
//    bar_first()
//
//    println("Second")
//    bar_second()
//  }

//  def example() : Unit = {
////    val r: Spore[String, String] =
////      spore {
////        x => capture(x).replace('a', 'b')
////      }
//
////      val a: Spore[String, Int] = spore {
////        (x:String) => x.length
////      }
//
//  }
//
  def example1() : Unit = {
    val s = spore {
      val y = 3
        (x: Int) => {
        x * y
      }
    }

    val s2: Spore[String, Int] = spore {
      val y = 3
      (x: String) => {
        x.length
      }
    }
  }
//
//  def examples2() : Unit = {
//    val s = spore {
//      (x: Int) => {
//        x
//      }
//    }
//  }
//
//  def examples3() : Unit = {
//    val s = spore {
//      val y = 3
//      (x: Int) => {
//        // x * y
//        A.foo(x, y)
//      }
//    }
//  }

  // all objects, that are referenced within a spore need to be declared
  // either as val or as parameter


}