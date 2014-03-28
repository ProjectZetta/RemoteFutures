/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.controller.calculator

import concurrent.{Future, Promise}

/**
 * @author Marvin Hansen
 */

trait TransportationSimilarityCalculator extends TA_Array {

  private final val _ONE: Double = 1.0
  private final val _NULL: Double = 0.0

  protected def calcTransportationSimilarityArrayFut(refTrans: Int, c2Trans: Array[Int], isSet: Boolean, w: Double, ret: Array[Double]): Future[Array[Double]] = {
    Promise.successful(calcTransportationSimilarityArray(refTrans, c2Trans, isSet, w, ret)).future
  }

  /** @param refTrans
    * @param c2Trans
    * @param isSet
    * @param w
    * @return*/
  protected def calcTransportationSimilarityArray(refTrans: Int, c2Trans: Array[Int], isSet: Boolean, w: Double, ret: Array[Double]): Array[Double] = {

    for (i ← 1 to c2Trans.size - 1) yield {
      ret(i) = getTransportationSimilarity(refTrans, c2Trans(i), isSet, w)
    }
    ret
  }


  /** returns the similarity score for transportation */
  protected def getTransportationSimilarity(refTrans: Int, c2Trans: Int, isSet: Boolean, w: Double): Double = {
    if (isSet) {
      if (refTrans == c2Trans) _ONE * w
      else lookupTransportSimilarity(refTrans, c2Trans) * w
    }
    else _NULL
  }
}

protected trait TA_Array {
  private final val _NULL = 0.0
  private final val _CAR = 1
  private final val _COACH = 2
  private final val _PLANE = 3
  private final val _TRAIN = 4
  private final val taArr = Array.ofDim[Double](12, 9)


  protected def lookupTransportSimilarity(refTA: Int, c2TA: Int): Double = {
    if (taArr.isEmpty) {
      initTA
      taArr(refTA)(c2TA)
    }
    else {
      taArr(refTA)(c2TA)

    }
  }

  protected def initTA {

    taArr(_COACH)(_CAR) = 0.75
    taArr(_COACH)(_PLANE) = _NULL
    taArr(_COACH)(_TRAIN) = _NULL
    //
    taArr(_CAR)(_COACH) = 0.75
    taArr(_CAR)(_TRAIN) = 0.50
    taArr(_CAR)(_PLANE) = _NULL
    //
    taArr(_PLANE)(_COACH) = _NULL
    taArr(_PLANE)(_CAR) = _NULL
    taArr(_PLANE)(_TRAIN) = 0.25
    //
    taArr(_TRAIN)(_COACH) = _NULL
    taArr(_TRAIN)(_CAR) = 0.50
    taArr(_TRAIN)(_PLANE) = 0.25
  }

}
