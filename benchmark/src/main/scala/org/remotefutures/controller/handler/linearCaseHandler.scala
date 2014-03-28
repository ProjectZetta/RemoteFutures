/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.controller.handler

import org.remotefutures.controller.reasoner.CaseReasonerI
import org.remotefutures.model.{FeatureWeights, CaseMap}
import org.remotefutures.controller.manager.CaseManager

/**
 * linear execution.
 *
 * @author Marvin Hansen
 */
class linearCaseHandler(cr: CaseReasonerI) extends CaseHandlerI {
  def calcMostSimilarCases(refCases: CaseMap, cm: CaseManager, w: FeatureWeights) {

    val resList = cm.createNewCaseMap(2 * refCases.size())
    for (j <- 1 to refCases.size()) {
      resList.addCase(cm.getCase(j))
      val simCase = cr.getMostSimilarCase(cm.getCase(j), cm, w)
      resList.addCase(simCase)
    }
  }

  override def finalize() {
    super.finalize()
  }
}
