/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.controller.reasoner

import org.remotefutures.model.{FeatureWeights, Case}
import org.remotefutures.controller.manager.CaseManager

/**
 * @author Marvin Hansen
 */
trait CaseReasonerI {
  /**
   * @param refCase reference case
   * @param cm case manager
   * @param weights weights for case properties
   * @return most similar case
   */
  def getMostSimilarCase(refCase: Case, cm: CaseManager, weights: FeatureWeights): Case

  /**
   * @param nrCases number of similar cases that shall be returned
   * @param refCase reference case
   * @param cm case manager
   * @param weights weights for case properties
   * @return hasmap of most similar case to reference case
   **/
  def getMostSimilarCases(nrCases: Int, refCase: Case, cm: CaseManager, weights: FeatureWeights): CaseMap
}

