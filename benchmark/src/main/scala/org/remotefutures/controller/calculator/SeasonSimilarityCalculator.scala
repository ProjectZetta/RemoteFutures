/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.controller.calculator

/**
 * @author Marvin Hansen
 */

object SeasonSimilarityCalculator extends SeasonSimilarityCalculator

trait SeasonSimilarityCalculator {

  private final val _NULL: Double = 0.0
  private final val _ONE: Double = 1.0
  private final val JANUARY: Int = 1
  private final val FEBRUARY: Int = 2
  private final val MARCH: Int = 3
  private final val APRIL: Int = 4
  private final val MAY: Int = 5
  private final val JUNE: Int = 6
  private final val JULY: Int = 7
  private final val AUGUST: Int = 8
  private final val SEPTEMBER: Int = 9
  private final val OCTOBER: Int = 10
  private final val NOVEMBER: Int = 11
  private final val DECEMBER: Int = 12

  private final val DBG = false

  /**
   * @param refSeason reference season to score
   * @param refHT     reference HolitdayType. Season score depends on HT.
   * @param c2Season  season to compare
   * @param isSet
   * @param w
   * @return
   */
  protected def calcSeasonSimilarityArray(refSeason: Int, refHT: Int, c2Season: Array[Int], isSet: Boolean, w: Double, ret: Array[Double]): Unit = {

    for (i ← 1 to c2Season.size - 1) yield {
      ret(i) = getSeasonSimilarity(refSeason, refHT, c2Season(i), isSet, w)
    }
  }

  /** Calculates the similarity of the seasons depending on the type of
    * holiday. However, season differences between southern and northern hemisphere are
    * not covered yet.
    * @param refSeason reference season
    * @param refHT reference holiday type
    * @param c2Season season to compare to
    * @return season similarity score
    */
  protected def getSeasonSimilarity(refSeason: Int, refHT: Int, c2Season: Int, isSet: Boolean, w: Double): Double = {

    if (isSet) {
      if (c2Season == refSeason) _ONE * w
      else {
        getAllSeasonSimilarity(refSeason, refHT, c2Season) * w
      }
    }
    else _NULL
  }

  /** @param c2Season; season to score
    * @return season similarity score */
  private def getAllSeasonSimilarity(refSeason: Int, refHT: Int, c2Season: Int): Double = refHT match {

    case 1 => getACTIVESeasonSimilarity(refSeason, c2Season)
    case 2 => getBATHINGSeasonSimilarity(refSeason, c2Season)
    case 3 => getCITYSeasonSimilarity(refSeason, c2Season)
    case 4 => getEDUCATIONSeasonSimilarity(refSeason, c2Season)
    case 5 => getLANGUAGESeasonSimilarity(refSeason, c2Season)
    case 6 => getRECREATIONSeasonSimilarity(refSeason, c2Season)
    case 7 => getSKIINGSeasonSimilarity(refSeason, c2Season)
    case 8 => getWANDERINGSeasonSimilarity(refSeason, c2Season)
  }

  private def getSKIINGSeasonSimilarity(refSeason: Int, c2Season: Int): Double = {
    if (c2Season == refSeason - 1 || c2Season == refSeason + 1 && (c2Season == DECEMBER || c2Season == JANUARY || c2Season == FEBRUARY)) 0.91
    else if (c2Season == refSeason - 2 || c2Season == refSeason + 2 && (c2Season == DECEMBER || c2Season == JANUARY || c2Season == FEBRUARY)) 0.81
    else if (c2Season == refSeason - 3 || c2Season == refSeason + 3 && (c2Season == DECEMBER || c2Season == JANUARY || c2Season == FEBRUARY)) 0.71
    else if (c2Season == refSeason && (c2Season == MAY || c2Season == JUNE || c2Season == JULY || c2Season == AUGUST || c2Season == SEPTEMBER)) _NULL
    else _NULL
  }

  private def getWANDERINGSeasonSimilarity(refSeason: Int, c2Season: Int): Double = {
    if (c2Season == refSeason - 1 || c2Season == refSeason + 1 && (c2Season == JUNE || c2Season == JULY || c2Season == AUGUST || c2Season == SEPTEMBER)) 0.92
    else if (c2Season == refSeason - 2 || c2Season == refSeason + 2 && (c2Season == JUNE || c2Season == JULY || c2Season == AUGUST || c2Season == SEPTEMBER)) 0.82
    else if (c2Season == refSeason - 3 || c2Season == refSeason + 3 && (c2Season == JUNE || c2Season == JULY || c2Season == AUGUST || c2Season == SEPTEMBER)) 0.72
    else if (c2Season == refSeason - 4 || c2Season == refSeason + 4 && (c2Season == JUNE || c2Season == JULY || c2Season == AUGUST || c2Season == SEPTEMBER)) 0.62
    else if (c2Season == NOVEMBER || c2Season == DECEMBER || c2Season == JANUARY || c2Season == FEBRUARY) _NULL
    else 0.51
  }

  private def getACTIVESeasonSimilarity(refSeason: Int, c2Season: Int): Double = {
    if (c2Season == refSeason - 1 || c2Season == refSeason + 1 && (c2Season == JUNE || c2Season == JULY || c2Season == AUGUST || c2Season == SEPTEMBER)) 0.93
    else if (c2Season == refSeason - 2 || c2Season == refSeason + 2 && (c2Season == JUNE || c2Season == JULY || c2Season == AUGUST || c2Season == SEPTEMBER)) 0.83
    else if (c2Season == refSeason - 3 || c2Season == refSeason + 3 && (c2Season == JUNE || c2Season == JULY || c2Season == AUGUST || c2Season == SEPTEMBER)) 0.73
    else if (c2Season == refSeason - 4 || c2Season == refSeason + 4 && (c2Season == JUNE || c2Season == JULY || c2Season == AUGUST || c2Season == SEPTEMBER)) 0.63
    else if (c2Season == NOVEMBER || c2Season == DECEMBER || c2Season == JANUARY || c2Season == FEBRUARY) _NULL
    else 0.52
  }

  private def getBATHINGSeasonSimilarity(refSeason: Int, c2Season: Int): Double = {
    if (c2Season == refSeason - 1 || c2Season == refSeason + 1 && (c2Season == JUNE || c2Season == JULY || c2Season == AUGUST || c2Season == SEPTEMBER)) 0.94
    else if (c2Season == refSeason - 2 || c2Season == refSeason + 2 && (c2Season == MAY || c2Season == JUNE || c2Season == JULY || c2Season == AUGUST || c2Season == SEPTEMBER)) 0.84
    else if (c2Season == refSeason - 3 || c2Season == refSeason + 3 && (c2Season == APRIL || c2Season == MAY || c2Season == JUNE || c2Season == JULY || c2Season == AUGUST || c2Season == SEPTEMBER)) 0.74
    else if (c2Season == NOVEMBER || c2Season == DECEMBER || c2Season == JANUARY || c2Season == FEBRUARY) _NULL
    else 0.23
  }

  private def getCITYSeasonSimilarity(refSeason: Int, c2Season: Int): Double = {
    if ((c2Season == refSeason + 1) || c2Season == refSeason - 1) 0.95
    else if ((c2Season == refSeason + 2) || c2Season == refSeason - 2) 0.75
    else if ((c2Season == refSeason + 3) || c2Season == refSeason - 3) 0.55
    else if ((c2Season == refSeason + 4) || c2Season == refSeason - 4) 0.35
    else _NULL
  }

  private def getEDUCATIONSeasonSimilarity(refSeason: Int, c2Season: Int): Double = {
    if ((c2Season == refSeason + 1) || c2Season == refSeason - 1) 0.96
    else if ((c2Season == refSeason + 2) || c2Season == refSeason - 2) 0.86
    else if ((c2Season == refSeason + 3) || c2Season == refSeason - 3) 0.76
    else if ((c2Season == refSeason + 4) || c2Season == refSeason - 4) 0.66
    else 0.26
  }

  private def getLANGUAGESeasonSimilarity(refSeason: Int, c2Season: Int): Double = {
    if ((c2Season == refSeason + 1) || c2Season == refSeason - 1) 0.97
    else if ((c2Season == refSeason + 2) || c2Season == refSeason - 2) 0.88
    else if ((c2Season == refSeason + 3) || c2Season == refSeason - 3) 0.78
    else if ((c2Season == refSeason + 4) || c2Season == refSeason - 4) 0.68
    else 0.37
  }

  private def getRECREATIONSeasonSimilarity(refSeason: Int, c2Season: Int): Double = {
    if (c2Season == refSeason - 1 || c2Season == refSeason + 1 && (c2Season == JUNE || c2Season == JULY || c2Season == AUGUST || c2Season == SEPTEMBER)) 0.99
    else if (c2Season == refSeason - 2 || c2Season == refSeason + 2 && (c2Season == JUNE || c2Season == JULY || c2Season == AUGUST || c2Season == SEPTEMBER)) 0.89
    else if (c2Season == refSeason - 3 || c2Season == refSeason + 3 && (c2Season == JUNE || c2Season == JULY || c2Season == AUGUST || c2Season == SEPTEMBER)) 0.79
    else if (c2Season == refSeason - 4 || c2Season == refSeason + 4 && (c2Season == JUNE || c2Season == JULY || c2Season == AUGUST || c2Season == SEPTEMBER)) 0.69
    else if (c2Season == NOVEMBER || c2Season == DECEMBER || c2Season == JANUARY || c2Season == FEBRUARY) _NULL
    else 0.49
  }
}