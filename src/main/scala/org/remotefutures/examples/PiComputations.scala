/*
 * Copyright (c) 2014 Martin Senne
 */
package org.remotefutures.examples

import java.math.MathContext

import scala.annotation.tailrec
import scala.math.BigDecimal.RoundingMode
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import org.remotefutures.core.EnvironmentImplicits.DefaultConfigBasedRemoteExecutionContext
import org.remotefutures.core.RemoteFuture


/**
 * Contains all methods for computiong PI by various methods..
 */
object PiComputations extends Serializable {

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
    val pi = ((arctan1_5 * BigDecimal(4)) - (arctan1_239)) * BigDecimal(4)
    return pi.setScale(digits, BigDecimal.RoundingMode.HALF_UP);
  }


  /**
   * Calculate sum elements from start to start+nrOfElements.
   *
   * @param start
   * @param nrOfElements
   * @return
   */
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
  private def arctan(inverseX: Int, scale:Int): BigDecimal = {

    val mc: java.math.MathContext = new MathContext(scale, java.math.RoundingMode.HALF_EVEN)

    // val roundingMode = BigDecimal.RoundingMode.HALF_EVEN

    val invX = BigDecimal(inverseX, mc);
    val invX2 = BigDecimal(inverseX * inverseX, mc);

    // var numer = BigDecimal(1).divide(invX, scale, roundingMode);
    var numer = BigDecimal(1, mc) / invX
    var result = numer;
    var i:Int = 1
    var term= BigDecimal(0.0, mc)
    val zero = BigDecimal(0, mc)
    var termIsNotZero: Boolean = true
    do {
      // numer = numer.divide(invX2, scale, roundingMode);
      numer = numer / invX2
      println("1." + numer + " " + numer.scale)
      numer = numer(mc)
      println("2." + numer + " " + numer.scale)
      numer = numer.setScale( scale, RoundingMode.HALF_EVEN)
      println("3." + numer + " " + numer.scale)

      val denom = 2 * i + 1;
      // term = numer.divide(BigDecimal.valueOf(denom), scale, roundingMode);
      val term = numer / BigDecimal(denom, mc)
      if ((i % 2) != 0) {
        result = result - term
      } else {
        result = result + term
      }
      i = i + 1
      println("Scala: " + i + " Numer: " + numer + " " + " term: " + term + " result " + result)
      println("               " + "Numer: " + numer.scale + " " + " term: " + term.scale + " result " + result.scale)

      termIsNotZero = (term.compare(zero) != 0)

    } while ( termIsNotZero )

    // } while ( term.compare(zero) != 0 )
    result
  }
}

object RunPi {
  import JavaPi._

  def main(args: Array[String]) : Unit = {

    println( "PI: " + PiComputations.computePiFor(0, 10000))

    val digits = 5
    println( "PI: " + JavaPi.computePi(digits))
    println( "PI: " + PiComputations.computePi(digits))

  }
}
