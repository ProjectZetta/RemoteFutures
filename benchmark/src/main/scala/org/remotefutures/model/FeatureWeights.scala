/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.model

import org.remotefutures.model.enums.Weight._
import org.remotefutures.model.enums._


/**
 * @author Marvin Hansen
 */

class DefaultFeatureWeights {

  private final val defaultWeight: Weight = Weight.HIGH
  private final val defaultSet: Boolean = true
  private final val durationW = Weights(defaultWeight, defaultSet)
  private final val nrPersonsW = Weights(defaultWeight, defaultSet)
  private final val regionW = Weights(defaultWeight, defaultSet)
  private final val priceW = Weights(defaultWeight, defaultSet)
  private final val seasonW = Weights(defaultWeight, defaultSet)
  private final val accW = Weights(defaultWeight, defaultSet)
  private final val hotelW = Weights(defaultWeight, defaultSet)
  private final val holidayTyW = Weights(defaultWeight, defaultSet)
  private final val transW = Weights(defaultWeight, defaultSet)
  private final val countryW = Weights(defaultWeight, defaultSet)


  def getDefaultFeatureWeights: FeatureWeights =
    new FeatureWeights(durationW, nrPersonsW, regionW, priceW, seasonW, accW, hotelW, holidayTyW, transW, countryW)
}

final case class FeatureWeights(durationWeight: Weights,
                                nrOfPersonsWeight: Weights,
                                regionWeight: Weights,
                                priceWeight: Weights,
                                seasonWeight: Weights,
                                accommodationWeight: Weights,
                                hotelWeight: Weights,
                                holidayTypeWeight: Weights,
                                transportationWeight: Weights,
                                countryW: Weights) {

  // lazy private final val is a constant that gets evaluated at first access and then replaced with its value which means
  // the calculation is performed once at first access which speeds up object creation due to late evaluation and
  // saves tons of computation time and makes the application significant faster.
  lazy val SUM = sumAllSetWeights()

  private def sumAllSetWeights(): Double = {
    /* summing an array is noticeable faster compared to filter and summing up a list*/
    val arr: Array[Int] = new Array[Int](10)
    if (durationWeight.checkSet) arr(0) = durationWeight.weight.id
    if (nrOfPersonsWeight.checkSet) arr(1) = nrOfPersonsWeight.weight.id
    if (regionWeight.checkSet) arr(2) = regionWeight.weight.id
    if (priceWeight.checkSet) arr(3) = priceWeight.weight.id
    if (seasonWeight.checkSet) arr(4) = seasonWeight.weight.id
    if (accommodationWeight.checkSet) arr(5) = accommodationWeight.weight.id
    if (hotelWeight.checkSet) arr(6) = hotelWeight.weight.id
    if (holidayTypeWeight.checkSet) arr(7) = holidayTypeWeight.weight.id
    if (transportationWeight.checkSet) arr(8) = transportationWeight.weight.id
    if (countryW.checkSet) arr(9) = countryW.weight.id
    arr.sum
  }
}

final case class Weights(weight: Weight, checkSet: Boolean)
