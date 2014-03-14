/*
 * Copyright (c) 2014 Martin Senne, Marvin Hansen.
 */
package org.remotefutures.examples

import scala.annotation.tailrec
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import org.remotefutures.core.RemoteFuture.rfuture
import org.remotefutures.core.EnvironmentImplicits.ConfigFileBaseRemoteExecutionContext


/**
  */
object RemoteExampleFibonacci extends App {

  val n = 1000000000 // fib is 546875
  remFib(n)

  /* Fibonacci code from
   * http://peter-braun.org/2012/06/fibonacci-numbers-in-scala/
   */
  def fib(n: Int): Int = {
    @tailrec
    def fib_tail(n: Int, a: Int, b: Int): Int = n match {
      case 0 => a
      case _ => fib_tail(n - 1, b, (a + b) % 1000000)
    }
    fib_tail(n % 1500000, 0, 1)
  }


  def remFib(m: Int) = {
    val rmt = rfuture {
      fib(m)
    }

    rmt onComplete {
      case Success(res) =>
        println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
        println("Fibonacci number is: " + res)
        println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")

      case Failure(t) => println("An error happened: " + t.getMessage)
    }

  }
}
