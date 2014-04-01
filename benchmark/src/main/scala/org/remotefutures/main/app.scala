/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.main

import java.io.File
import org.remotefutures.controller.manager.CaseManager
import java.util.concurrent.TimeUnit
import org.remotefutures.controller.reasoner._
import org.remotefutures.controller.handler._

/**
 * @author Marvin Hansen
 */
object app extends App {

  // path to store excel files in case data export is switched on
  private[this] final val p = "benchmark/Results/"
  //path to data file
  private[this] final val data: File = new File("benchmark/data.xml")
  println("Data file is:")
  println(data.toString)
  assert(data != null)

  /*
  * Correct benchmark execution:
  * Chose at least 60 pre-runs
  * Chose an appropriate number of runs, i.e. 100
  * Disable println (verbose = off)
  *
  * See doc/Benchmark.md for details
  * Note, off-set has to be smaller compared to iterations.
  */
  private[this] final val OFF_Set: Int = 10
  private[this] final val iterations: Int = 10
  //// Flags to switch on / off the benchmarks to run
  private[this] val runALL: Boolean = false
  private[this] final val runLin: Boolean = false
  private[this] final val runParArr: Boolean = true
  private[this] final val runFutureParArr: Boolean = true
  //// set the handler to use for all measurements except linear.
  //// Currently, ParColCaseHandler is the fastest one.
  private[this] final val handler = new ParColCaseHandler
  // switched console output on or off. Should be false for any real measurement
  private[this] final val verbose = true
  // TimeUnit for measurements
  private[this] final val t = TimeUnit.SECONDS
  // switches export to excel on or off
  private[this] final val EXCL_STATS = false
  private[this] final val bm = new Benchmark(t, EXCL_STATS)
  private[this] final val cm = new CaseManager(data)


  if (runALL || runLin) {
    //Linear base case for measuring no concurrency / parallelism at all.
    // Bear in mind, this takes ages to complete so better switch it off
    // unless required. Having said, this one reflects the "base" version
    // all other implementations are compared to.
    lazy val lin_name = "Linear"
    lazy val lin_data_out: File = new File(p + "lin_data.xml")
    lazy val lin_scala_stats: File = new File(p + "lin.xls")
    lazy val lin_handler = new linearCaseHandler
    //
    lazy val cr = new CaseReasoner_lin

    /* linear run */
    runBenchmark(lin_name, cr, lin_handler, lin_scala_stats, lin_data_out)
    //cleanup
    handler.finalize()
    cleanUp()
  }


  if (runALL || runParArr) {
    //parallel collections
    lazy val aos_name = "Par Arr"
    lazy val aos_data_out: File = new File(p + "Par_Arr.xml")
    lazy val aos_scala_stats: File = new File(p + "Par_Arr.xls")
    //
    lazy val cr = new CaseReasoner_Par_ARR

    /* parallel collection run */
    runBenchmark(aos_name, cr, handler, aos_scala_stats, aos_data_out)
    //cleanup
    handler.finalize()
    cleanUp()
  }


  if (runALL || runFutureParArr) {
    //parallel case
    lazy val fut_arr_name = "Futures Par Arr"
    lazy val fut_arr_data_out: File = new File(p + "future_par_arr.xml")
    lazy val fut_arr_stats: File = new File(p + "futures_par_arr.xls")
    //
    lazy val cr = new CaseReasoner_Futures_ARR
    /* future par. coll run */
    runBenchmark(fut_arr_name, cr, handler, fut_arr_stats, fut_arr_data_out)
    //cleanup
    cleanUp()
  }

  /**
   * Triggers garbage collection and finalizes handler
   */
  private[this] def cleanUp() {
    Runtime.getRuntime.gc()
    Thread.sleep(500)
    handler.finalize()
  }

  /**
   * @deprecated
   *
   * Might be removed in the near future
   *
   * @param cRef
   * @param cTest
   * @param cTestName
   */
  private[this] def runReasonerVerification(cRef: CaseReasonerI, cTest: CaseReasonerI, cTestName: String) {
    println("verifying case reasoner: " + cTestName)
    bm.verifyReasoner(cRef, cTest, data)
    println("DONE, all good!")
    println()

  }

  private[this] def runBenchmark(name: String, cr: CaseReasonerI, ch: CaseHandlerI, statsFile: File, outfile: File) {

    bm.execute(verbose, name, cm, cr, ch, statsFile, outfile, iterations, OFF_Set)

  }

}
