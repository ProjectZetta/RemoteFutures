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
class ParColCaseHandler extends CaseHandlerI {

  override def calcMostSimilarCases(cr: CaseReasonerI, refCases: CaseMap, cm: CaseManager, w: FeatureWeights): Unit = {

    val resList = cm.createNewCaseMap(2 * refCases.size())

    for (j <- (1 to refCases.size()).par) yield {
      resList.addCase(cm.getCase(j))
      val simCase = cr.getMostSimilarCase(cm.getCase(j), cm, w)
      resList.addCase(simCase)
    }
  }

  override def finalize() {
    super.finalize()
  }
}
