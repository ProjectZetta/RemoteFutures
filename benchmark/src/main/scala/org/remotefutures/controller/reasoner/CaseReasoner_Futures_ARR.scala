/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.controller.reasoner

import org.remotefutures.model.{CaseMap, Case, FeatureWeights, CaseArrays}
import org.remotefutures.utils.SortableValueMap
import scala.concurrent.{Promise, ExecutionContext, Await, Future}
import org.remotefutures.controller.manager.CaseManager
import java.util.concurrent.{TimeUnit, Executors}
import org.remotefutures.controller.calculator.SimilarityCalculation_AOS
import scala.concurrent.duration.Duration

/**
 * @author Marvin Hansen
 */

class CaseReasoner_Futures_ARR extends CaseReasoner_Futures_ARRImpl

trait CaseReasoner_Futures_ARRImpl extends CaseReasonerI with SimilarityCalculation_AOS {

  private final val nrCores = 2 * Runtime.getRuntime.availableProcessors()
  private[this] final val T = Duration(3, TimeUnit.SECONDS)
  private final implicit val xc = ExecutionContext fromExecutorService Executors.newFixedThreadPool(nrCores)

  /**
   * @param refCase reference case
   * @param cm case manager
   * @param weights weights for case properties
   * @return list of most similar case to reference case
   */
  override def getMostSimilarCase(refCase: Case, cm: CaseManager, weights: FeatureWeights): Case = {

    val ca = cm.getCaseArrays
    val scm: SortableValueMap[Int, Double] = getSimilarCases(refCase, ca, weights)
    // pull the best matching case from the list
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

    val ca = cm.getCaseArrays
    val scm: SortableValueMap[Int, Double] = getSimilarCases(refCase, ca, weights)
    val ret: CaseMap = cm.createNewCaseMap(nrCases)
    val it = scm.keySet().iterator() // keysIterator

    var idx: Int = 0
    for (i <- 1 to nrCases) {

      idx = it.next() // get next key from most similar cases
      ret.addCase(cm.getCase(i)) // pull corresponding case from case map
    }
    ret
  }

  private def getSimilarCases(refCase: Case, ca: CaseArrays, weights: FeatureWeights): SortableValueMap[Int, Double] = {

    getSimilarCasesArr(refCase, ca, weights)
  }


  private def getSimilarCasesArr(refCase: Case, ca: CaseArrays, weights: FeatureWeights): SortableValueMap[Int, Double] = {

    val scm: SortableValueMap[Int, Double] = new SortableValueMap[Int, Double](ca.size)
    // it's just one future call versus 1024 in the non ARR-version
    val f: Future[Array[Double]] = calcSimilarityArrayFut(refCase, ca, weights).mapTo[Array[Double]]
    // it's blocking here
    val arr: Array[Double] = Await.result(f, T)
    // iterating in parallel, to speed things up
    for (j <- (1 to arr.size - 1).par) yield {
      scm.put(j, arr(j))
    }
    scm.sortByDescendingValue()
    scm.remove(refCase.journeyCode)
    scm
  }

  def calcSimilarityArrayFut(refCase: Case, c2: CaseArrays, w: FeatureWeights): Future[Array[Double]] = {

    Promise.successful(calcSimilarity(refCase, c2, w)).future
  }


}