/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.controller.calculator

/**
 * @author Marvin Hansen
 */
object DurationSimilarityCalculator extends DurationSimilarityCalculator

trait DurationSimilarityCalculator {

  private final val _NULL: Double = 0.0
  private final val _ONE: Double = 1.0

  /**
   * @param refDuration reference duration
   * @param c2Dur an array containg all duration to compare
   * @param isSet boolean flag if this field is set
   * @param w weight
   * @return a double array of similarity scores for each duration value
   */
  protected def calcDurationSimilarityArray(refDuration: Short, c2Dur: Array[Short], isSet: Boolean, w: Double, ret: Array[Double]): Unit = {

    for (i ← 1 to c2Dur.size - 1) yield {
      ret(i) = getDurationSimilarity(refDuration, c2Dur(i), isSet, w)
    }
    //ret
  }


  /** @param refDuration reference duration
    * @param c2Dur duration to compare
    * @param isSet boolean flag if this field is set
    * @param w weight
    * @return similarity score */
  protected def getDurationSimilarity(refDuration: Short, c2Dur: Short, isSet: Boolean, w: Double): Double = {

    val DBG = false
    if (DBG) {
      println("ref Duration is: " + refDuration)
      println("compare Duration is: " + c2Dur)
      println("dur. set is : " + isSet)
      println("dur. weight is : " + w)
    }

    if (isSet) {
      if (refDuration == c2Dur) _ONE * w
      else if (c2Dur == refDuration + 1 || c2Dur == refDuration - 1) 0.9 * w
      else if (c2Dur == refDuration + 2 || c2Dur == refDuration - 2) 0.8 * w
      else if (c2Dur == refDuration + 3 || c2Dur == refDuration - 3) 0.7 * w
      else if (c2Dur == refDuration + 4 || c2Dur == refDuration - 4) 0.5 * w
      else if (c2Dur == refDuration + 5 || c2Dur == refDuration - 5) 0.3 * w
      else if (c2Dur == refDuration + 6 || c2Dur == refDuration - 6) 0.2 * w
      else if (c2Dur == refDuration + 7 || c2Dur == refDuration - 7) 0.1 * w
      else _NULL
    }
    else _NULL
  }
}
