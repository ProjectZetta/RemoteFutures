/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.controller.reasoner

import org.remotefutures.utils.SortableValueMap
import org.remotefutures.model.{CaseMap, Case, CaseArrays, FeatureWeights}
import org.remotefutures.controller.manager.CaseManager
import org.remotefutures.controller.calculator.SimilarityCalculation_AOS

/**
 * @author Marvin Hansen
 */

class CaseReasoner_Par_ARR extends CaseReasoner_Par_ARR_Impl

trait CaseReasoner_Par_ARR_Impl extends CaseReasonerI with SimilarityCalculation_AOS {

  /**
   * @param refCase reference case
   * @param cm case manager
   * @param w weights for case properties
   * @return list of most similar case to reference case
   */
  override def getMostSimilarCase(refCase: Case, cm: CaseManager, w: FeatureWeights): Case = {

    val cl = cm.getCaseArrays

    val scm: SortableValueMap[Int, Double] = getSimilarCases(refCase, cl, w)

    cm.getCase(scm.entrySet.iterator.next.getKey)
  }

  /**
   * @param nrCases number of similar cases that shall be returned
   * @param refCase reference case
   * @param cm case manager
   * @param weights weights for case properties
   * @return list of most similar case to reference case
   **/
  override def getMostSimilarCases(nrCases: Int, refCase: Case, cm: CaseManager, weights: FeatureWeights): CaseMap = {

    val cl = cm.getCaseArrays

    val scm: SortableValueMap[Int, Double] = getSimilarCases(refCase, cl, weights)
    val ret: CaseMap = cm.createNewCaseMap(nrCases)
    val it = scm.keySet().iterator() // keysIterator

    var idx: Int = 0
    for (i <- 1 to nrCases) {

      idx = it.next() // get next key from most similar cases
      ret.addCase(cm.getCase(i)) // pull corresponding case from case map
    }
    ret
  }


  /** Calculates similarity for each case and sorts all cases in descending
    * order according the level of similarity to th reference case.
    * @param refCase reference case
    * @param ca CaseArray
    * @param weights FeatureWeights */
  private[this] def getSimilarCases(refCase: Case, ca: CaseArrays, weights: FeatureWeights): SortableValueMap[Int, Double] = {

    val scm: SortableValueMap[Int, Double] = new SortableValueMap[Int, Double](ca.size)

    val arr = calcSimilarity(refCase, ca, weights)

    for (j <- (1 to arr.size - 1).par) yield {
      scm.put(j, arr(j))
    }
    scm.sortByDescendingValue()
    scm.remove(refCase.journeyCode)
    scm
  }

}
