/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.controller.calculator

/**
 * @author Marvin Hansen
 */
object PriceSimilarityCalculator extends PriceSimilarityCalculator

trait PriceSimilarityCalculator {

  private[this] final val _NULL: Double = 0.0

  /**
   * @param refPrice
   * @param c2Price
   * @param isSet
   * @param w
   * @return
   */
  protected def calcPriceSimilarityArray(refPrice: Double, c2Price: Array[Int], isSet: Boolean, w: Double, ret: Array[Double]): Unit = {
    for (i ← 1 to c2Price.size - 1) yield {
      ret(i) = getPriceSimilarity(refPrice, c2Price(i), isSet, w)
    }
  }


  /** @param c2Price price to score
    * @param isSet FeatureWeights
    * @return similarity score */
  protected def getPriceSimilarity(refPrice: Double, c2Price: Double, isSet: Boolean, w: Double): Double = {
    if (isSet) {
      if (c2Price == refPrice) 1.0 * w
      else if (checkRange(c2Price, refPrice, (refPrice - refPrice * 0.01), (refPrice + refPrice * 0.01))) 0.99 * w
      else if (checkRange(c2Price, refPrice, (refPrice - refPrice * 0.05), (refPrice + refPrice * 0.05))) 0.95 * w
      else if (checkRange(c2Price, refPrice, (refPrice - refPrice * 0.10), (refPrice + refPrice * 0.10))) 0.90 * w
      else if (checkRange(c2Price, refPrice, (refPrice - refPrice * 0.15), (refPrice + refPrice * 0.15))) 0.85 * w
      else if (checkRange(c2Price, refPrice, (refPrice - refPrice * 0.20), (refPrice + refPrice * 0.20))) 0.80 * w
      else if (checkRange(c2Price, refPrice, (refPrice - refPrice * 0.25), (refPrice + refPrice * 0.25))) 0.75 * w
      else if (checkRange(c2Price, refPrice, (refPrice - refPrice * 0.30), (refPrice + refPrice * 0.30))) 0.70 * w
      else if (checkRange(c2Price, refPrice, (refPrice - refPrice * 0.35), (refPrice + refPrice * 0.35))) 0.65 * w
      else if (checkRange(c2Price, refPrice, (refPrice - refPrice * 0.40), (refPrice + refPrice * 0.40))) 0.40 * w
      else if (checkRange(c2Price, refPrice, (refPrice - refPrice * 0.45), (refPrice + refPrice * 0.45))) 0.20 * w
      else if (checkRange(c2Price, refPrice, (refPrice - refPrice * 0.50), (refPrice + refPrice * 0.50))) 0.10 * w
      else _NULL
    }
    else _NULL
  }

  /** * compares whether a number is within a particular range.
    * @param price number to check
    * @param min min value
    * @param max max value
    * @return true if the value is within the range */
  private def checkRange(price: Double, refPrice: Double, min: Double, max: Double): Boolean = {
    if (price == refPrice) true
    else if (price <= max && price >= refPrice) true
    else if (price >= min && price <= refPrice) true
    else false
  }
}