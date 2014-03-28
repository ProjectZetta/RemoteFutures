/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.controller.handler

import org.remotefutures.controller.reasoner.CaseReasonerI
import org.remotefutures.model.{FeatureWeights, CaseMap}
import org.remotefutures.controller.manager.CaseManager

/**
 * @author Marvin Hansen
 */
class FutureCaseParColHandler(cr: CaseReasonerI) extends CaseHandlerI {

  override def calcMostSimilarCases(refCases: CaseMap, cm: CaseManager, w: FeatureWeights): Unit = {

    val resList = cm.createNewCaseMap(2 * refCases.size())

    for (i <- (1 to refCases.size()).par) yield {
      resList.addCase(cm.getCase(i))
      val simCase = cr.getMostSimilarCase(cm.getCase(i), cm, w)
      resList.addCase(simCase)
    }
  }

  override def finalize() {
    super.finalize()
  }
}