/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.controller.calculator

import concurrent.{Future, Promise}
import org.remotefutures.model._

/**
 * @author Marvin Hansen
 */
object SimilarityCalculator extends SimilarityCalculator

class SimilarityCalculator extends SimilarityCalculation

/**
 * initHT() needs to be called first before using this trait
 */
trait SimilarityCalculation extends AccommodationSimilarityCalculator
with HolidayTypeSimilarityCalculator with HotelSimilarityCalculator
with PersonsSimilarityCalculator with PriceSimilarityCalculator
with RegionSimilarityCalculator with SeasonSimilarityCalculator
with TransportationSimilarityCalculator with DurationSimilarityCalculator
with CountrySimilarityCalculator {


  def calcCaseSimilarityFut(refCase: Case, c2: Case, w: FeatureWeights): Future[Double] = {

    Promise.successful(calcSimilarity(refCase, c2, w)).future
  }


  /**
   *
   * @param c2 case for comparison
   * @param w; feature weights appliable for comparison
   * @return similarity score */
  def calcSimilarity(refCase: Case, c2: Case, w: FeatureWeights): Double =

    (getDurationSimilarity(refCase.duration, c2.duration, w.durationWeight.checkSet, w.durationWeight.weight.id)
      + getPersonsSimilarity(refCase.numberOfPersons, c2.numberOfPersons, w.nrOfPersonsWeight.checkSet, w.nrOfPersonsWeight.weight.id)
      + getPriceSimilarity(refCase.price, c2.price, w.priceWeight.checkSet, w.priceWeight.weight.id)
      + getRegionSimilarity(refCase.region, c2.region, w.regionWeight.checkSet, w.regionWeight.weight.id)
      + getTransportationSimilarity(refCase.transportation.id, c2.transportation.id, w.transportationWeight.checkSet, w.transportationWeight.weight.id)
      + getSeasonSimilarity(refCase.season.id, refCase.holidayType.id, c2.season.id, w.seasonWeight.checkSet, w.seasonWeight.weight.id)
      + getAccommodationSimilarity(refCase.accommodation.id, c2.accommodation.id, w.accommodationWeight.checkSet, w.accommodationWeight.weight.id)
      + getHotelSimilarity(refCase.hotel.toString, c2.hotel.toString, w.hotelWeight.checkSet, w.hotelWeight.weight.id)
      + getHolidayTypeSimilarity(refCase.holidayType.id, c2.holidayType.id, w.holidayTypeWeight.checkSet, w.holidayTypeWeight.weight.id)
      + getCountrySimilarity(refCase.country.countryCode, c2.country.countryCode, w.countryW.checkSet, w.countryW.weight.id)) / w.SUM
}
