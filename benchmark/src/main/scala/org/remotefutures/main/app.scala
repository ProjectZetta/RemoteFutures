/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.main

import java.io.File
import org.remotefutures.controller.manager.CaseManager
import java.util.concurrent.TimeUnit
import org.remotefutures.controller.reasoner.{CaseReasoner_lin, CaseReasonerI, CaseReasoner_Par_ARR}
import org.remotefutures.controller.handler._

/**
 * @author Marvin Hansen
 */
object app extends App {

  private[this] final val p = "benchmark/Results"
  //path
  private[this] final val data: File = new File("benchmark/" + "data.xml")
  println("Path is:")
  println(data.toString)
  assert(data != null)
  private[this] final val t = TimeUnit.SECONDS
  private[this] final val bm = new Benchmark(t)
  private[this] final val cm = new CaseManager(data)


  private[this] final val OFF_Set: Int = 10
  private[this] final val iterations: Int = 10

  private[this] val runReasonerVerifier: Boolean = false

  private[this] val runALL: Boolean = false
  private[this] final val runLin: Boolean = false
  private[this] final val runLinArr: Boolean = false
  private[this] final val runParArr: Boolean = false
  private[this] final val runFutureArr: Boolean = false
  private[this] final val runFutureParArr: Boolean = true
  private[this] final val runFutureParArrMapRed: Boolean = false

  //
  lazy val cr = new CaseReasoner_lin

  if (runReasonerVerifier) {
    val cRef = cr
    val cTest = new CaseReasoner_Par_ARR
    runReasonerVerification(cRef, cTest, "Lin_ARR")
  }


  if (runALL || runLin) {
    //Linear case
    lazy val lin_name = "Linear"
    lazy val lin_data_out: File = new File(p + "lin_data.xml")
    lazy val lin_scala_stats: File = new File(p + "lin.xls")
    lazy val lin_handler = new linearCaseHandler(cr)
    /* linear run */
    runBenchmark(lin_name, lin_handler, lin_scala_stats, lin_data_out)
    //cleanup
    lin_handler.finalize()
    cleanUp()
  }


  if (runALL || runLinArr) {
    //Linear case
    lazy val lin_arr_name = "Linear arr"
    lazy val lin_arr_data_out: File = new File(p + "lin_arr_data.xml")
    lazy val lin_arr_stats: File = new File(p + "lin_arr.xls")
    lazy val lin_handler = new linearCaseHandler(cr)
    /* linear run */
    runBenchmark(lin_arr_name, lin_handler, lin_arr_stats, lin_arr_data_out)
    //cleanup
    lin_handler.finalize()
    cleanUp()
  }

  if (runALL || runParArr) {
    //parallel case
    lazy val aos_name = "Par Arr"
    lazy val aos_data_out: File = new File(p + "Par_Arr.xml")
    lazy val aos_scala_stats: File = new File(p + "Par_Arr.xls")
    lazy val aos_handler = new ParColCaseHandler(cr)
    /* parallel collection run */
    runBenchmark(aos_name, aos_handler, aos_scala_stats, aos_data_out)
    //cleanup
    aos_handler.finalize()
    cleanUp()
  }

  if (runALL || runFutureArr) {
    //parallel case
    lazy val fut_arr_name = "Futures and Arr"
    lazy val fut_arr_data_out: File = new File(p + "future_arr.xml")
    lazy val fut_arr_stats: File = new File(p + "futures_arr.xls")
    lazy val fut_arr_handler = new FutureCaseHandler(cr)
    /* Future based run */
    runBenchmark(fut_arr_name, fut_arr_handler, fut_arr_stats, fut_arr_data_out)
    //cleanup
    cleanUp()
  }

  if (runALL || runFutureParArr) {
    //parallel case
    lazy val fut_arr_name = "Futures Par Arr"
    lazy val fut_arr_data_out: File = new File(p + "future_par_arr.xml")
    lazy val fut_arr_stats: File = new File(p + "futures_par_arr.xls")
    lazy val fut_arr_handler = new FutureCaseParColHandler(cr)
    /* future par. coll run */
    runBenchmark(fut_arr_name, fut_arr_handler, fut_arr_stats, fut_arr_data_out)
    //cleanup
    cleanUp()
  }

  if (runALL || runFutureParArrMapRed) {
    //parallel case
    lazy val fut_arr_name = "MapReduce Arr"
    lazy val fut_arr_data_out: File = new File(p + "future_par_arr_MR.xml")
    lazy val fut_arr_stats: File = new File(p + "futures_par_arr_MR.xls")
    lazy val fut_arr_handler = new MapReduceCaselHandler(cr)
    /* Map Reduce run */
    runBenchmark(fut_arr_name, fut_arr_handler, fut_arr_stats, fut_arr_data_out)
    //cleanup
    cleanUp()
  }


  private[this] def cleanUp() {
    Runtime.getRuntime.gc()
    Thread.sleep(500)
    cr.finalize()
  }

  private[this] def runReasonerVerification(cRef: CaseReasonerI, cTest: CaseReasonerI, cTestName: String) {
    println("verifying case reasoner: " + cTestName)
    bm.verifyReasoner(cRef, cTest, data)
    println("DONE, all good!")
    println()

  }

  private[this] def runBenchmark(name: String, ch: CaseHandlerI, statsFile: File, outfile: File) {

    bm.execute(verbose = true, name, cm, ch, statsFile, outfile, iterations, OFF_Set)

  }

}
