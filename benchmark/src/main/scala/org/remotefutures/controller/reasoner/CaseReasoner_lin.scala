/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.controller.reasoner

import org.remotefutures.utils.SortableValueMap
import org.remotefutures.model.{CaseMap, Case, FeatureWeights}
import org.remotefutures.controller.manager.CaseManager
import org.remotefutures.controller.calculator.SimilarityCalculation

/**
 * @author Marvin Hansen
 */
class CaseReasoner_lin extends CaseReasoner_lin_Impl

trait CaseReasoner_lin_Impl extends CaseReasonerI with SimilarityCalculation {


  /** */
  override def getMostSimilarCase(refCase: Case, cm: CaseManager, w: FeatureWeights) = {

    val cl = cm.getAllCases

    val svm: SortableValueMap[Int, Double] = getSimilarCases(refCase, cl, w)
    // pull the best matching case from the list
    cl.getCase(svm.entrySet.iterator.next.getKey)
  }

  /** */
  override def getMostSimilarCases(nrCases: Int, refCase: Case, cm: CaseManager, weights: FeatureWeights): CaseMap = {

    val cl = cm.getAllCases

    val scm: SortableValueMap[Int, Double] = getSimilarCases(refCase, cl, weights)
    val ret: CaseMap = cl.createNewCaseMap(nrCases)
    val it = scm.keySet().iterator() // keysIterator

    var idx: Int = 0
    for (i <- 1 to nrCases) {
      idx = it.next() // get next key from most similar cases
      ret.addCase(cl.getCase(i)) // pull corresponding case from case map
    }
    ret
  }


  /** Calculates similarity for each case and sorts all cases in descending
    * order according the level of similarity to the reference case.
    * @param refCase reference case
    * @param cl CaseList
    * @param weights FeatureWeights */
  private[this] def getSimilarCases(refCase: Case, cl: CaseMap, weights: FeatureWeights): SortableValueMap[Int, Double] = {

    val scm: SortableValueMap[Int, Double] = new SortableValueMap[Int, Double](cl.map.size)

    for (j <- 1 to cl.map.size) {
      //val simScore = calcSimilarity(refCase, cl.getCase(j), weights)
      scm.put(j, calcSimilarity(refCase, cl.getCase(j), weights))
    }
    scm.sortByDescendingValue()
    scm.remove(refCase.journeyCode)
    scm
  }

  override def finalize() {
    super.finalize()
  }

}
