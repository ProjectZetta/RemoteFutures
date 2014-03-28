/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.controller.calculator

/**
 * @author Marvin Hansen
 */
object HolidayTypeSimilarityCalculator extends HolidayTypeSimilarityCalculator

trait HolidayTypeSimilarityCalculator extends HT_Array {

  private final val _NULL: Double = 0.0
  private final val _ONE: Double = 1.0

  /**
   * @param refHT
   * @param c2Ht
   * @param isSet
   * @param w
   * @return
   */
  protected def calcHolidayTypeSimilarityArray(refHT: Int, c2Ht: Array[Int], isSet: Boolean, w: Double, ret: Array[Double]): Unit = {

    for (i ← 1 to c2Ht.size - 1) yield {
      ret(i) = getHolidayTypeSimilarity(refHT, c2Ht(i), isSet, w)
    }
    //ret
  }


  /** @param ht HolidayType to score
    * @return similarity score */
  protected def getHolidayTypeSimilarity(refHT: Int, ht: Int, isSet: Boolean, w: Double): Double = {
    if (isSet) {
      if (ht == refHT) _ONE * w
      else lookupHolidayTypeSimilarity(refHT, ht) * w
    }
    else _NULL
  }
}
