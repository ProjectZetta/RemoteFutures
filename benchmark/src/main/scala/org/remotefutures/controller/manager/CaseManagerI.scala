/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.controller.manager

import java.io.File
import org.remotefutures.model._
import org.remotefutures.model.enums.Transportation.Transportation
import org.remotefutures.model.enums.Month.Month
import org.remotefutures.model.Accommodation.Accommodation
import org.remotefutures.model.enums.HolidayType.HolidayType
import org.remotefutures.model.Case
import org.remotefutures.model.Hotel
import org.remotefutures.model.CaseMap
import org.remotefutures.model.Country

/**
 * @author Marvin Hansen
 *
 *         Interface
 */
trait CaseManagerI {

  def loadAllCases(file: File): CaseMap

  def saveCases(caseMap: CaseMap, file: File)

  def createNewCase(journeyCode: Int, caseId: String, holidayType: HolidayType,
                    price: Int, numberOfPersons: Short, region: String,
                    country: Country, transportation: Transportation,
                    duration: Short, season: Month, accommodation: Accommodation,
                    hotel: Hotel): Case

  def createNewCaseMap(size: Int): CaseMap

  def addCase(c: Case) {}

  def removeCase(c: Case)

  def getCase(i: Int): Case

  def getAllCases: CaseMap

  def getDefaultFeatureWeights: FeatureWeights
}
