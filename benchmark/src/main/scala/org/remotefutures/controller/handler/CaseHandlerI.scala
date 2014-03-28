/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.controller.handler

import org.remotefutures.model.{FeatureWeights, CaseMap}
import org.remotefutures.controller.manager.CaseManager
import org.remotefutures.controller.reasoner.CaseReasonerI

/**
 * @author Marvin Hansen
 */
object CaseHandler extends CaseHandler

abstract class CaseHandler

trait CaseHandlerI {
  def calcMostSimilarCases(cr: CaseReasonerI, refCases: CaseMap, cm: CaseManager, w: FeatureWeights): Unit
}


