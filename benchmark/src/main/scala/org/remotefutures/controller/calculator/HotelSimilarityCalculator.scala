/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.controller.calculator

/**
 * @author Marvin Hansen
 */
object HotelSimilarityCalculator extends HotelSimilarityCalculator

trait HotelSimilarityCalculator {

  private final val _NULL: Double = 0.0

  /** @param refHotel
    * @param c2Hotel
    * @param isSet
    * @param w
    * @return*/
  protected def calcHotelSimilarityArray(refHotel: String, c2Hotel: Array[String], isSet: Boolean, w: Double, ret: Array[Double]): Unit = {

    for (i ← 1 to c2Hotel.size - 1) yield {
      ret(i) = getHotelSimilarity(refHotel, c2Hotel(i), isSet, w)
    }
    //ret
  }

  /** @param hotel hotel to score
    * @return similarity score for the hotel */
  protected def getHotelSimilarity(refHotel: String, hotel: String, isSet: Boolean, w: Double): Double = {
    if (isSet) {
      if (hotel == refHotel) 1.0 * w
      else _NULL
    }
    else _NULL
  }
}
