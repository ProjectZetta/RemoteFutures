/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.controller.handler

import org.remotefutures.controller.reasoner.CaseReasonerI
import org.remotefutures.model.{Case, FeatureWeights, CaseMap}
import org.remotefutures.controller.manager.CaseManager
import java.util.concurrent.Executors

/**
 * @author Marvin Hansen
 */

class MapReduceCaselHandler extends CaseHandlerI {

  private final val splitFactor = 32 * Runtime.getRuntime.availableProcessors()
  private final val EXEC = Executors.newFixedThreadPool(splitFactor)

  override def calcMostSimilarCases(cr: CaseReasonerI, refCases: CaseMap, cm: CaseManager, w: FeatureWeights): Unit = {
    val res = cm.createNewCaseMap(2 * refCases.size())
    // scatter
    val arr = scatter(cr, refCases, cm, w)
    // gather
    val ret = gather(arr, res)
    // return result, if required
    //ret
  }


  private def gather(arr: Array[CaseMap], res: CaseMap): CaseMap = {
    for (i <- 1 to arr.size - 1) {
      res.map.putAll(arr(i).map)
    }
    res
  }

  private def scatter(cr: CaseReasonerI, refCases: CaseMap, cm: CaseManager, w: FeatureWeights): Array[CaseMap] = {
    val splitSize = refCases.size() / splitFactor
    val arr: Array[CaseMap] = new Array[CaseMap](splitFactor + 1)

    for (i <- (1 to splitFactor).par) yield {
      val startIdx = ((i - 1) * splitSize) + 1
      val stopIdx = i * splitSize

      val resMap = cm.createNewCaseMap(2 * splitSize) //
      //val k = new Kernel("Kernel_" + i, startIdx, stopIdx, refCases, resMap, cm, w, cr)
      //val res = k.run()
      arr(i) = calcSim(startIdx, stopIdx, refCases, resMap, cm, w, cr)
    }

    arr
  }

  /**
   * load balanced kernel executor. Total fair scheduling based on
   * modulo arithmetic. This is a proof of concept for load balancing
   * of distributed kernels or even hetrogenous GPU  / CPU / remote kernels.
   *
   * @param idx index of current kernel
   * @param nrKernels nr. of all kernels
   * @param kernels Array containing all kernels
   */
  private def execute(idx: Int, nrKernels: Int, kernels: Array[Kernel]) = {

    /*
    * modulo load balancing;
    *
    * Plus one is required because nrKernels mod nrKernels
    * or n * nrKernels is always null means that kernel never gets
    * executed. However,
    *
    * (n -1) * nrKernels
    *
    * returns the correct kernel ID thus n+1 is the fix to go.
    */
    val k = kernels(idx % (nrKernels + 1))
    EXEC.execute(k)
  }


  def calcSim(startIdx: Int, stopIdx: Int, subMap: CaseMap, resMap: CaseMap, cm: CaseManager, w: FeatureWeights, cr: CaseReasonerI) = {

    for (i <- startIdx to stopIdx) {
      resMap.addCase(subMap.getCase(i))
      val simCase: Case = cr.getMostSimilarCase(subMap.getCase(i), cm, w)
      //resMap.addCase(simCase)
      resMap.map.put(i, simCase)
    }
    resMap
  }

  override def finalize(): Unit = {
    super.finalize()
  }


  private class kernelProcessor(nrKernels: Int, kernels: Array[Kernel]) extends Runnable {

    // for runnable
    override def run(): Unit = {

      while (true) {

      }
    }

  }

  /**
   *
   * @param id   kernel id
   * @param cr   case reasoner
   */
  private class Kernel(id: Int, cr: CaseReasonerI) extends Runnable {
    def ID = id

    override def run(): Unit = ???

    def run(startIdx: Int, stopIdx: Int, resMap: CaseMap, caseMap: CaseMap, cm: CaseManager, w: FeatureWeights): CaseMap = {
      var i = startIdx
      while (i < stopIdx + 1) {
        resMap.addCase(caseMap.getCase(i))
        val simCase: Case = cr.getMostSimilarCase(caseMap.getCase(i), cm, w)
        resMap.map.put(i, simCase)
        i = i + 1
      }
      resMap
    }

  }

}
