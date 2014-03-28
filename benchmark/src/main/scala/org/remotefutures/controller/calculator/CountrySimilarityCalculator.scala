/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.controller.calculator

/**
 * @author Marvin Hansen
 */

object CountrySimilarityCalculator extends CountrySimilarityCalculator

trait CountrySimilarityCalculator extends CA_Array {

  private final val _NULL: Double = 0.0
  private final val _ONE: Double = 1.0


  protected def calcCountrySimilarityArray(refCountryCode: String, c2CountryCode: Array[String], isSet: Boolean, w: Double, ret: Array[Double]): Unit = {

    for (i <- 1 to c2CountryCode.size - 1) yield {
      ret(i) = getCountrySimilarity(refCountryCode, c2CountryCode(i), isSet, w)
    }
  }


  protected def getCountrySimilarity(refCountryCode: String, c2CountryCode: String, isSet: Boolean, w: Double): Double = {

    if (isSet) {
      if (refCountryCode == c2CountryCode) _ONE * w
      else if (refCountryCode == "N/A" || c2CountryCode == "N/A") _NULL
      else lookupCountrySimilarity(refCountryCode, c2CountryCode) * w
    }
    else _NULL
  }
}

trait CA_Array {
  private final val _MAP = initMap()
  //
  private final val EG = convert("EG")
  private final val BE = convert("BE")
  private final val DK = convert("DK")
  private final val DE = convert("DE")
  private final val FR = convert("FR")
  private final val GR = convert("GR")
  private final val GB = convert("GB")
  private final val IE = convert("IE")
  private final val NL = convert("NL")
  private final val IT = convert("IT")
  private final val MT = convert("MT")
  private final val AT = convert("AT")
  private final val PT = convert("PT")
  private final val SE = convert("SE")
  private final val CH = convert("CH")
  private final val ES = convert("ES")
  private final val CZ = convert("CZ")
  private final val TR = convert("TR")
  private final val TN = convert("TN")
  private final val HN = convert("HN")
  private final val PL = convert("PL")
  private final val SK = convert("SK")
  private final val BG = convert("BG")


  private final val _ARR = initCA() // Array.ofDim[Double](50, 9)


  protected def lookupCountrySimilarity(refCountryCode: String, c2CountryCode: String): Double = {
    _ARR(convert(refCountryCode))(convert(c2CountryCode))
  }

  private def convert(in: String): Int = {
    _MAP.get(in)
  }

  private def initMap() = {
    val map = new java.util.HashMap[String, Int](30)

    map.put("EG", 1) // Egypt
    map.put("BE", 2) // Belgium
    map.put("DK", 3) // Denmark
    map.put("DE", 4) // Germany
    map.put("FR", 5) // France
    map.put("GR", 6) // Greece
    map.put("GB", 7) // UK
    map.put("IE", 8) // Ireland
    map.put("NL", 9) // Netherlands
    map.put("IT", 10) // Italy
    map.put("MT", 11) // Malta
    map.put("AT", 12) // Austria
    map.put("PT", 13) // Portugal
    map.put("SE", 14) // Sweden
    map.put("CH", 15) // Switzerland
    map.put("ES", 16) // Spain
    map.put("CZ", 17) // Czech Republic
    map.put("TR", 18) // Turkey
    map.put("TN", 19) // Tunesia
    map.put("HN", 20) // Hungary
    map.put("PL", 21) // Poland
    map.put("SK", 22) // Slovakia
    map.put("BG", 23) // Bulgaria

    map
  }

  private def initCA() = {

    val caArr = Array.ofDim[Double](50, 25)

    caArr(PL)(SK) = 0.85
    caArr(PL)(CZ) = 0.80
    caArr(PL)(HN) = 0.78
    caArr(HN)(PL) = 0.78
    caArr(PL)(BG) = 0.73
    caArr(BG)(PL) = 0.73
    caArr(BG)(ES) = 0.21
    caArr(ES)(BG) = 0.21
    caArr(SK)(PL) = 0.85
    caArr(CZ)(PL) = 0.80
    caArr(PT)(ES) = 0.90
    caArr(ES)(PT) = 0.90
    caArr(IT)(ES) = 0.80
    caArr(ES)(IT) = 0.80
    caArr(IT)(GR) = 0.70
    caArr(GR)(IT) = 0.70
    caArr(PT)(GR) = 0.60
    caArr(GR)(PT) = 0.60
    caArr(ES)(GR) = 0.40
    caArr(GR)(ES) = 0.40
    caArr(TN)(MT) = 0.75
    caArr(MT)(TN) = 0.75
    caArr(TN)(EG) = 0.86
    caArr(EG)(TN) = 0.86
    caArr(EG)(TR) = 0.73
    caArr(TR)(EG) = 0.73
    caArr(BG)(EG) = 0.37
    caArr(EG)(GB) = 0.37
    caArr(AT)(CH) = 0.84
    caArr(CH)(AT) = 0.84
    caArr(DE)(AT) = 0.77
    caArr(AT)(DE) = 0.77
    caArr(CH)(DE) = 0.59
    caArr(DE)(CH) = 0.59
    caArr(BE)(NL) = 0.88
    caArr(NL)(BE) = 0.88
    caArr(BE)(FR) = 0.71
    caArr(FR)(BE) = 0.71
    caArr(BE)(DE) = 0.64
    caArr(DE)(BE) = 0.64
    caArr(BE)(AT) = 0.29
    caArr(AT)(BE) = 0.29
    caArr(IE)(GB) = 0.89
    caArr(GB)(IE) = 0.89
    caArr(DK)(SE) = 0.91
    caArr(SE)(DK) = 0.91
    caArr(DE)(DK) = 0.79
    caArr(DK)(DE) = 0.79
    caArr(DE)(SE) = 0.69
    caArr(SE)(DE) = 0.69

    caArr
  }
}
