/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.controller.calculator

/**
 * @author Marvin Hansen
 */
object RegionSimilarityCalculator extends RegionSimilarityCalculator

trait RegionSimilarityCalculator {

  private final val _NULL: Double = 0.0

  /**
   * @param refRegion reference region to score
   * @param c2Reg an array of region to compare
   * @param isSet boolean flag if region is set
   * @param w feature weight
   * @return an array of similarity scores for each region
   */
  protected def calcRegionSimilarityArray(refRegion: String, c2Reg: Array[String], isSet: Boolean, w: Double, ret: Array[Double]): Unit = {
    for (i ← 1 to c2Reg.size - 1) yield {
      ret(i) = getRegionSimilarity(refRegion, c2Reg(i), isSet, w)
    }
    //ret
  }


  /** @param c2Reg region to score
    * @param isSet FeatureWeights
    * @return similarity score */
  protected def getRegionSimilarity(refRegion: String, c2Reg: String, isSet: Boolean, w: Double): Double = {
    if (isSet)
      if (c2Reg == refRegion) 1.0 * w
      else _NULL
    else _NULL
  }
}


