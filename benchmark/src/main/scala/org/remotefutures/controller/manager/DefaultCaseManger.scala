/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.controller.manager

import java.io.File
import org.remotefutures.model._
import org.remotefutures.model.Accommodation.Accommodation
import org.remotefutures.model.enums.Month.Month
import org.remotefutures.model.enums.Transportation.Transportation
import org.remotefutures.model.enums.HolidayType.HolidayType


/**
 * @author Marvin Hansen
 */

/** default implementation using a xml statsFile to store data */
class DefaultCaseManger(source: File) extends CaseManagerI {

  private final val XM: FileManagerI = new CaseFileManager
  private final val CM: CaseMap = loadCases(source)
  private final val FM: DefaultFeatureWeights = new DefaultFeatureWeights

  private final val ARR_SIZE = CM.size() + 1
  private final val caseIDArr = Array.ofDim[String](ARR_SIZE)
  private final val htArr = Array.ofDim[Int](ARR_SIZE)
  private final val priceArr = Array.ofDim[Int](ARR_SIZE)
  private final val persArr = Array.ofDim[Short](ARR_SIZE)
  private final val regArr = Array.ofDim[String](ARR_SIZE)
  private final val transArr = Array.ofDim[Int](ARR_SIZE)
  private final val durArr = Array.ofDim[Short](ARR_SIZE)
  private final val seasArr = Array.ofDim[Int](ARR_SIZE)
  private final val accArr = Array.ofDim[Int](ARR_SIZE)
  private final val hotArr = Array.ofDim[String](ARR_SIZE)
  private final val countryArr = Array.ofDim[String](ARR_SIZE)


  //
  private final val CASE_ARRAYS = createArrays(CM)

  override def loadAllCases(file: File): CaseMap = loadCases(file)

  private def loadCases(file: File): CaseMap = XM.read(file)

  override def saveCases(caseMap: CaseMap, file: File) {
    XM.write(caseMap, file)
  }

  override def createNewCase(journeyCode: Int, caseId: String, holidayType: HolidayType,
                             price: Int, numberOfPersons: Short, region: String,
                             country: Country, transportation: Transportation,
                             duration: Short, season: Month, accommodation: Accommodation,
                             hotel: Hotel): Case = {
    new Case(journeyCode, caseId, holidayType, price, numberOfPersons, region,
      country, transportation, duration, season, accommodation, hotel)
  }

  override def createNewCaseMap(size: Int): CaseMap = CM.createNewCaseMap(size)

  override def addCase(c: Case) {
    CM.addCase(c)
  }

  override def removeCase(c: Case) {
    CM.removeCase(c.journeyCode)
  }

  override def getCase(i: Int): Case = CM.getCase(i)

  override def getAllCases: CaseMap = CM

  override def getDefaultFeatureWeights: FeatureWeights = FM.getDefaultFeatureWeights

  def getCaseArrays: CaseArrays = CASE_ARRAYS

  private def createArrays(cm: CaseMap): CaseArrays = {

    for (id <- (1 to cm.map.size)) {

      val c = cm.getCase(id)
      assert(id == c.journeyCode)
      caseIDArr(id) = c.caseId
      htArr(id) = c.holidayType.id
      priceArr(id) = c.price
      persArr(id) = c.numberOfPersons
      regArr(id) = c.region
      transArr(id) = c.transportation.id
      durArr(id) = c.duration
      seasArr(id) = c.season.id
      accArr(id) = c.accommodation.id
      hotArr(id) = c.hotel.toString
      countryArr(id) = c.country.countryCode
    }
    new CaseArrays(caseIDArr, htArr, priceArr, persArr, regArr, transArr, durArr, seasArr, accArr, hotArr, countryArr)
  }
}
