/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.controller.handler

import java.util.concurrent.TimeUnit
import concurrent.duration.Duration
import org.remotefutures.controller.reasoner.CaseReasonerI
import org.remotefutures.model.{FeatureWeights, CaseMap}
import org.remotefutures.controller.manager.CaseManager

/**
 * @author Marvin Hansen
 */
class FutureCaseHandler(cr: CaseReasonerI) extends CaseHandlerI {


  final val T = Duration(3, TimeUnit.SECONDS)

  override def calcMostSimilarCases(refCases: CaseMap, cm: CaseManager, w: FeatureWeights): Unit = {


    val resList = cm.createNewCaseMap(2 * refCases.size())

    for (i ← 1 to refCases.size()) yield {

      val simCase = cr.getMostSimilarCase(cm.getCase(i), cm, w)
      resList.addCase(cm.getCase(i))
      resList.addCase(simCase)
    }
  }

  override def finalize() {
    super.finalize()
  }
}