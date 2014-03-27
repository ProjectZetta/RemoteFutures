/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.model

/**
 * @author Marvin Hansen
 */
final case class CaseArrays(caseIDArr: Array[String],
                            htArr: Array[Int],
                            priceArr: Array[Int],
                            persArr: Array[Short],
                            regArr: Array[String],
                            transArr: Array[Int],
                            durArr: Array[Short],
                            seasArr: Array[Int],
                            accArr: Array[Int],
                            hotArr: Array[String],
                            countryArr: Array[String]
                             ) {

  lazy val size = getSize

  // All arrays must have the same size so taking any size is fine
  private def getSize: Int = caseIDArr.size
}