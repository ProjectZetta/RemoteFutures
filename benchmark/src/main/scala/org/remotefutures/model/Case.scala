/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.model

import enums.Month.Month
import enums.HolidayType.HolidayType
import enums.Transportation.Transportation
import Accommodation.Accommodation

/**
 * @author Marvin Hansen
 */
final case class Case(journeyCode: Int, caseId: String,
                      holidayType: HolidayType, price: Int,
                      numberOfPersons: Short, region: String,
                      country: Country, transportation: Transportation,
                      duration: Short, season: Month,
                      accommodation: Accommodation, hotel: Hotel)
