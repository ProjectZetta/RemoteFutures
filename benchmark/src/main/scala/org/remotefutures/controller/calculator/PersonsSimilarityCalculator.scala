/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.controller.calculator

/**
 * @author Marvin Hansen
 */
object PersonsSimilarityCalculator extends PersonsSimilarityCalculator

trait PersonsSimilarityCalculator {

  private final val _NULL: Double = 0.0
  private final val _ONE: Double = 1.0


  protected def calcPersonsSimilarityArray(refPersons: Short, c2Persons: Array[Short], isSet: Boolean, w: Double, ret: Array[Double]): Unit = {

    for (i ← 1 to c2Persons.size - 1) yield {
      ret(i) = getPersonsSimilarity(refPersons, c2Persons(i), isSet, w)
    }
    //ret
  }


  /** @param c2Persons nr persons
    * @param isSet FeatureWeights
    * @return similarity score */
  protected def getPersonsSimilarity(refPersons: Short, c2Persons: Short, isSet: Boolean, w: Double): Double = {

    if (isSet) {
      if (c2Persons == refPersons) _ONE * w
      else if (c2Persons == refPersons + 1 || c2Persons == refPersons - 1) 0.90 * w
      else if (c2Persons == refPersons + 2 || c2Persons == refPersons - 2) 0.70 * w
      else if (c2Persons == refPersons + 3 || c2Persons == refPersons - 3) 0.50 * w
      else if (c2Persons == refPersons + 4 || c2Persons == refPersons - 4) 0.30 * w
      else if (c2Persons == refPersons + 5 || c2Persons == refPersons - 5) 0.05 * w
      else _NULL
    }
    else _NULL
  }
}
