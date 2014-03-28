/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.controller.calculator

/**
 * @author Marvin Hansen
 */
object HT_Array extends HT_Array

trait HT_Array {

  private final val _NULL: Double = 0.0
  private final val ACTIVE: Int = 1
  private final val BATHING: Int = 2
  private final val CITY: Int = 3
  private final val EDUCATION: Int = 4
  private final val LANGUAGE: Int = 5
  private final val RECREATION: Int = 6
  private final val SKIING: Int = 7
  private final val WANDERING: Int = 8
  //
  private final val arr = initHT()

  protected def lookupHolidayTypeSimilarity(refHT: Int, c2HT: Int): Double = {
    arr(refHT)(c2HT)
  }

  private def initHT() = {

    val htArr = Array.ofDim[Double](60, 9)

    htArr(ACTIVE)(BATHING) = 0.77
    htArr(BATHING)(ACTIVE) = 0.77

    htArr(ACTIVE)(CITY) = 0.81
    htArr(CITY)(ACTIVE) = 0.81

    htArr(ACTIVE)(EDUCATION) = 0.71
    htArr(EDUCATION)(ACTIVE) = 0.71

    htArr(ACTIVE)(LANGUAGE) = 0.66
    htArr(LANGUAGE)(ACTIVE) = 0.66

    htArr(ACTIVE)(RECREATION) = 0.79
    htArr(RECREATION)(ACTIVE) = 0.79

    htArr(ACTIVE)(SKIING) = 0.91
    htArr(SKIING)(ACTIVE) = 0.91

    htArr(ACTIVE)(WANDERING) = 0.79
    htArr(WANDERING)(ACTIVE) = 0.79

    htArr(BATHING)(CITY) = 0.55
    htArr(CITY)(BATHING) = 0.55

    htArr(BATHING)(EDUCATION) = 0.39
    htArr(EDUCATION)(BATHING) = 0.39

    htArr(BATHING)(LANGUAGE) = 0.20
    htArr(LANGUAGE)(BATHING) = 0.20

    htArr(BATHING)(RECREATION) = 0.93
    htArr(RECREATION)(BATHING) = 0.93

    htArr(BATHING)(SKIING) = _NULL
    htArr(SKIING)(BATHING) = _NULL

    htArr(BATHING)(WANDERING) = _NULL
    htArr(WANDERING)(BATHING) = _NULL

    htArr(CITY)(EDUCATION) = 0.76
    htArr(EDUCATION)(CITY) = 0.76

    htArr(CITY)(LANGUAGE) = 0.88
    htArr(LANGUAGE)(CITY) = 0.88

    htArr(CITY)(RECREATION) = 0.72
    htArr(RECREATION)(CITY) = 0.72

    htArr(CITY)(SKIING) = _NULL
    htArr(SKIING)(CITY) = _NULL

    htArr(CITY)(WANDERING) = _NULL
    htArr(WANDERING)(CITY) = _NULL

    htArr(EDUCATION)(LANGUAGE) = 0.96
    htArr(LANGUAGE)(EDUCATION) = 0.96

    htArr(EDUCATION)(RECREATION) = _NULL
    htArr(RECREATION)(EDUCATION) = _NULL

    htArr(EDUCATION)(SKIING) = _NULL
    htArr(SKIING)(EDUCATION) = _NULL

    htArr(EDUCATION)(WANDERING) = _NULL
    htArr(WANDERING)(EDUCATION) = _NULL

    htArr(LANGUAGE)(RECREATION) = 0.59
    htArr(RECREATION)(LANGUAGE) = 0.59

    htArr(LANGUAGE)(SKIING) = _NULL
    htArr(SKIING)(LANGUAGE) = _NULL

    htArr(LANGUAGE)(WANDERING) = _NULL
    htArr(WANDERING)(LANGUAGE) = _NULL

    htArr(RECREATION)(SKIING) = 0.94
    htArr(SKIING)(RECREATION) = 0.94

    htArr(RECREATION)(WANDERING) = 0.78
    htArr(WANDERING)(RECREATION) = 0.78

    htArr(SKIING)(WANDERING) = _NULL
    htArr(WANDERING)(SKIING) = _NULL
    //
    htArr
  }

  override def finalize() {
    super.finalize()
  }
}
