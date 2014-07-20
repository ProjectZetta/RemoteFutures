/*
 * Copyright (c) 2014 Martin Senne, Marvin Hansen.
 */
package org.remotefutures.examples

import java.math.MathContext

import scala.annotation.tailrec
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import org.remotefutures.core.EnvironmentImplicits.DefaultConfigBasedRemoteExecutionContext
import org.remotefutures.core.RemoteFuture


/**
 *
 */
object Computations extends Serializable {

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

  /**
   * Compute the value of pi to the specified number of digits after the
   * decimal point. The value is computed using Machin's formula:
   * pi/4 = 4*arctan(1/5) - arctan(1/239)
   * and a power series expansion of arctan(x) to sufficient precision.
   */
  def computePi(digits: Int): BigDecimal = {
    val scale = digits + 5
    val arctan1_5 = arctan(5, scale)
    val arctan1_239 = arctan(239, scale)
    val pi = (arctan1_5 * BigDecimal(4)) - (arctan1_239 * BigDecimal (4))
    return pi.setScale(digits, BigDecimal.RoundingMode.HALF_UP);
  }

  def computePiFor(start: Int, nrOfElements: Int): Double = {
    var acc = 0.0
    for (i ← start until (start + nrOfElements))
      acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1)
    acc
  }



  /**
   * Compute the value, in radians, of the arctangent of the inverse of the
   * supplied integer to the specified number of digits after the decimal
   * point. The value is computed using the power series expansion for the arc
   * tangent:
   * arctan(x) = x - (x^3)/3 + (x^5)/5 - (x^7)/7 + (x^9)/9 ...
   */
  def arctan(inverseX: Int, scale:Int): BigDecimal = {

    val mc: java.math.MathContext = new MathContext(scale, java.math.RoundingMode.HALF_EVEN)

    // val roundingMode = BigDecimal.RoundingMode.HALF_EVEN

    val invX = BigDecimal(inverseX, mc);
    val invX2 = BigDecimal(inverseX * inverseX, mc);

    // var numer = BigDecimal(1).divide(invX, scale, roundingMode);
    var numer = BigDecimal(1, mc) / invX
    var result = numer;
    var i:Int = 1
    var term= BigDecimal(0.0, mc)
    do {
      // numer = numer.divide(invX2, scale, roundingMode);
      numer = numer / invX2
      val denom = 2 * i + 1;
      // term = numer.divide(BigDecimal.valueOf(denom), scale, roundingMode);
      term = numer / BigDecimal(denom, mc)
      if ((i % 2) != 0) {
        result = result - term
      } else {
        result = result + term
      }
      i = i + 1
      println("Scala: " + i + " Numer: " + numer + " " + " term: " + term + " result " + result)
    } while (term.compare(BigDecimal(0, mc)) != 0);
    result;
  }
}

object TestFibo {
  import JavaPi._

  def main(args: Array[String]) : Unit = {
//    println("100: " + Computations.fibBigInt((100)))
//    println("10000: " + Computations.fibBigInt((10000)))
//    println("1000000: " + Computations.fibBigInt((1000000)))
//    println("100000000: " + Computations.fibBigInt((100000000)))


    // println( "PI: " + Computations.computePiFor(0, 10000))

    println( "PI: " + JavaPi.computePi(5))
    println( "PI: " + Computations.computePi(5))

    // println("Arctan(3, 10) :   " + Computations.arctan(3, 10))
    // println( "PI: " + Computations.computePi(10))
  }
}
