/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.controller.calculator

/**
 * @author Marvin Hansen
 */
object AccommodationSimilarityCalculator extends AccommodationSimilarityCalculator

trait AccommodationSimilarityCalculator {

  private final val _NULL: Double = 0.0

  /**
   * @param refAcc
   * @param c2Acc
   * @param isSet
   * @param w
   * @return
   */
  protected[calculator] def calcAccommodationSimilarityArray(refAcc: Int, c2Acc: Array[Int], isSet: Boolean, w: Double, ret: Array[Double]): Unit = {

    for (i ← 1 to c2Acc.size - 1) yield {
      ret(i) = getAccommodationSimilarity(refAcc, c2Acc(i), isSet, w)
    }
    //ret
  }

  /** Accommodation similarity is build on top of the hotel star ranking. one
    * or two stars difference affects similarity by ten percent. three to five
    * stars difference affects similarity by twenty percent to cut off extreme
    * outliers.
    * @return similarity score */
  protected[calculator] def getAccommodationSimilarity(refAcc: Int, c2Acc: Int, isSet: Boolean, w: Double): Double = {

    if (isSet) {
      if (c2Acc == refAcc) 1.0 * w
      else if (c2Acc == refAcc) 0.90 * w
      else if (c2Acc == refAcc + 1 || c2Acc == refAcc - 1) 0.90 * w
      else if (c2Acc == refAcc + 2 || c2Acc == refAcc - 2) 0.70 * w
      else if (c2Acc == refAcc + 3 || c2Acc == refAcc - 3) 0.50 * w
      else if (c2Acc == refAcc + 4 || c2Acc == refAcc - 4) 0.10 * w
      else _NULL
    }
    else _NULL
  }
}
