/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.controller.calculator

import concurrent.{Promise, Future}
import org.remotefutures.model._

/**
 * @author Marvin Hansen
 */

object SimilarityCalculator_AOS extends SimilarityCalculator_AOS

class SimilarityCalculator_AOS extends SimilarityCalculation_AOS

trait SimilarityCalculation_AOS extends AccommodationSimilarityCalculator
with HolidayTypeSimilarityCalculator with HotelSimilarityCalculator
with PersonsSimilarityCalculator with PriceSimilarityCalculator
with RegionSimilarityCalculator with SeasonSimilarityCalculator
with TransportationSimilarityCalculator with DurationSimilarityCalculator
with CountrySimilarityCalculator {

  def calcSimilarity(refCase: Case, c2: CaseArrays, w: FeatureWeights): Array[Double] = {
    calcSimilarityArray(refCase, c2, w)
  }


  private def calcSimilarityArray(refCase: Case, c2: CaseArrays, w: FeatureWeights): Array[Double] = {

    // create arrays
    val ret: Array[Double] = new Array[Double](c2.size)
    val accSimilarityScores: Array[Double] = new Array[Double](c2.size)
    val durSimilarityScores = new Array[Double](c2.size)
    val perSimilarityScores = new Array[Double](c2.size)
    val priceSimilarityScores = new Array[Double](c2.size)
    val regSimilarityScores = new Array[Double](c2.size)
    val transSimilarityScores = new Array[Double](c2.size)
    val seasonSimilarityScores = new Array[Double](c2.size)
    val hotelSimilarityScores = new Array[Double](c2.size)
    val HT_SimilarityScores = new Array[Double](c2.size)
    val countrySimilarityScores = new Array[Double](c2.size)

    // pre fetch values
    val refAcc: Int = refCase.accommodation.id
    val setAcc: Boolean = w.accommodationWeight.checkSet
    val wAcc: Int = w.accommodationWeight.weight.id
    //
    val refDur: Short = refCase.duration
    val setDur: Boolean = w.durationWeight.checkSet
    val wDur: Int = w.durationWeight.weight.id
    //
    val refPer: Short = refCase.numberOfPersons
    val setPer: Boolean = w.nrOfPersonsWeight.checkSet
    val wPer: Int = w.nrOfPersonsWeight.weight.id
    //
    val refPrice: Int = refCase.price
    val setPrice: Boolean = w.priceWeight.checkSet
    val wPrice: Int = w.priceWeight.weight.id
    //
    val refReg: String = refCase.region
    val setReg: Boolean = w.regionWeight.checkSet
    val wReg: Int = w.regionWeight.weight.id
    //
    val refTrans: Int = refCase.transportation.id
    val setTrans: Boolean = w.transportationWeight.checkSet
    val wTrans: Int = w.transportationWeight.weight.id
    //
    val refSeas: Int = refCase.season.id
    val setSeas: Boolean = w.seasonWeight.checkSet
    val wSeas: Int = w.seasonWeight.weight.id
    //
    val refHot: String = refCase.hotel.toString
    val setHot: Boolean = w.hotelWeight.checkSet
    val wHot: Int = w.hotelWeight.weight.id
    //
    val refHt: Int = refCase.holidayType.id
    val setHt: Boolean = w.holidayTypeWeight.checkSet
    val wHt: Int = w.holidayTypeWeight.weight.id
    //
    val refCountryCode = refCase.country.countryCode
    val setCountry = w.countryW.checkSet
    val wCountry = w.countryW.weight.id

    // init arrays
    calcAccommodationSimilarityArray(refAcc, c2.accArr, setAcc, wAcc, accSimilarityScores)
    calcDurationSimilarityArray(refDur, c2.durArr, setDur, wDur, durSimilarityScores)
    calcHolidayTypeSimilarityArray(refHt, c2.htArr, setHt, wHt, HT_SimilarityScores)
    calcHotelSimilarityArray(refHot, c2.hotArr, setHot, wHot, hotelSimilarityScores)
    calcPersonsSimilarityArray(refPer, c2.persArr, setPer, wPer, perSimilarityScores)
    calcPriceSimilarityArray(refPrice, c2.priceArr, setPrice, wPrice, priceSimilarityScores)
    calcRegionSimilarityArray(refReg, c2.regArr, setReg, wReg, regSimilarityScores)
    calcSeasonSimilarityArray(refSeas, refHt, c2.seasArr, setSeas, wSeas, seasonSimilarityScores)
    calcTransportationSimilarityArray(refTrans, c2.transArr, setTrans, wTrans, transSimilarityScores)
    calcCountrySimilarityArray(refCountryCode, c2.countryArr, setCountry, wCountry, countrySimilarityScores)

    //calculate similarity score over all local similarites for each case
    for (i ← 1 to c2.size - 1) yield {
      ret(i) =
        (accSimilarityScores(i) +
          durSimilarityScores(i) +
          perSimilarityScores(i) +
          priceSimilarityScores(i) +
          regSimilarityScores(i) +
          seasonSimilarityScores(i) +
          transSimilarityScores(i) +
          hotelSimilarityScores(i) +
          HT_SimilarityScores(i) +
          countrySimilarityScores(i)) / w.SUM
    }
    ret
  }

}
