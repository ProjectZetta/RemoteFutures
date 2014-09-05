/*
 * Copyright (c) 2014 Martin Senne
 */
package org.remotefutures.examples

import java.math.MathContext

import scala.annotation.tailrec
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import org.remotefutures.core.EnvironmentImplicits.configBasedRemoteExecutionContext
import org.remotefutures.core.RemoteFuture


/**
 * Example computations which calculate Fibonacci numbers.
 */
object FibonacciComputations extends Serializable {

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

  def fibLong(n: Long): Long = {
    @tailrec
    def fib_tail(n: Long, a: Long, b: Long): Long = n match {
      case 0 => a
      case _ => fib_tail(n - 1, b, (a + b))
    }
    fib_tail(n, 0, 1)
  }

  def fibBigInt(n: Long): BigInt = {
    @tailrec
    def fib_tail(n: Long, a: BigInt, b: BigInt): BigInt = n match {
      case 0 => a
      case _ => fib_tail(n - 1, b, (a + b))
    }
    fib_tail(n, 0, 1)
    }
}

object RunFibo {

  def main(args: Array[String]) : Unit = {
    println("100: " + FibonacciComputations.fibBigInt((100)))
    println("10000: " + FibonacciComputations.fibBigInt((10000)))
    println("1000000: " + FibonacciComputations.fibBigInt((1000000)))
    println("100000000: " + FibonacciComputations.fibBigInt((100000000)))
  }
}
