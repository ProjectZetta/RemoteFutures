/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.main

import java.util.concurrent.TimeUnit
import org.remotefutures.controller.reasoner.CaseReasonerI
import java.io.File
import org.remotefutures.controller.manager.CaseManager
import org.remotefutures.model._
import org.remotefutures.utils._
import org.remotefutures.controller.handler.CaseHandlerI

/**
 * @author Marvin Hansen
 */
class Benchmark(t: TimeUnit) {

  private final val ST = TimeUnit.SECONDS

  private final val MIN = 1
  private final val MAX = 1024

  private final val EXCL_STATS = false


  /** verify a new case reasoner against a reference (CRef) */
  def verifyReasoner(cRef: CaseReasonerI, cTest: CaseReasonerI, dataFile: File) {

    val cm = new CaseManager(dataFile)
    val fw: FeatureWeights = cm.getDefaultFeatureWeights
    val stopper: StopWatch = new StopWatch

    println("Start Reasoner verification")
    stopper.start()

    for (i <- 1 to 1024) yield {
      val refCase = cm.getCase(i)
      val refSim = cRef.getMostSimilarCase(refCase, cm, fw)
      val testSim = cTest.getMostSimilarCase(refCase, cm, fw)
      if (refSim.journeyCode != testSim.journeyCode) {
        println("ERROR")
        println("TESTED CASE REASONER RETURNED WRONG SIMILAR CASE")
        println("==============================")
        println("REF CASE: " + refCase.toString)
        println("CASE SHOULD BE: " + refSim)
        println("BUT ACTUAL CASE WAS: " + testSim)
        println("==============================")
        assert(refSim.journeyCode == testSim.journeyCode)
        System.exit(-32)

      }

      //println(i + ") Case has most similar case: " +refSim.journeyCode )
    }
    stopper.stop()
    println("Stop verification! ")
    println("Verification took: " + stopper.getElapsedTime(ElapsedTime.EXEC_TIME, t) + " " + t.toString)

  }

  /** verify all cases */
  def verifyCases(cr: CaseReasonerI, dataFile: File) {

    val cm = new CaseManager(dataFile)
    val fw: FeatureWeights = cm.getDefaultFeatureWeights
    val stopper: StopWatch = new StopWatch

    println("Start verification")
    stopper.start()

    for (j <- (1 until 1024).par) yield {
      val refCase = cm.getCase(j)
      val simCase = cr.getMostSimilarCase(cm.getCase(j), cm, fw)
      assert(refCase != simCase)
    }
    stopper.stop()
    println("Stop verification! ")
    println("Verification took: " + stopper.getElapsedTime(ElapsedTime.EXEC_TIME, t) + " " + t.toString)
  }


  def execute(verbose: Boolean, name: String, cm: CaseManager, cr: CaseReasonerI, handler: CaseHandlerI, statsFile: File, outfile: File, iterations: Int, OFF_SET: Int): StatsData = {

    require(iterations >= OFF_SET, "OFF_SET must be less then iterations to prevent division by zero exception")

    val totalOps = (iterations * 1024 * 1024).toLong
    val cmap = cm.getAllCases
    val w: FeatureWeights = cm.getDefaultFeatureWeights

    val stats: Stats = new Stats(name, iterations, t)
    val stopper: StopWatch = new StopWatch

    if (verbose) {
      println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
      println("Benchmark for: " + name)
      println("Off-set: " + OFF_SET)
      println("Iterations: " + iterations)
      println("Time unit: " + t.toString)
      println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
      println("Start benchmarking...")
    }
    stopper.start()
    //=================================

    for (k <- 0 until iterations + OFF_SET) yield {

      if (k >= OFF_SET) {
        stats.start()

        handler.calcMostSimilarCases(cr, cmap, cm, w)

        stats.stop(k - OFF_SET)
      }
    }

    //=================================
    stopper.stop()
    val durTime = stopper.getElapsedTime(ElapsedTime.EXEC_TIME, ST).toLong
    val throughput = calcThroughput(durTime, totalOps)
    if (verbose) {
      // stats.printCpuTime
      if (EXCL_STATS) {
        stats.safeAll(statsFile)
      }
      stats.printExecutionTime()
      println("Done! Benchmarking execution time " + name + " took: " + durTime + " " + ST.toString)
      println("Throughput is: " + throughput + " OPS/" + ST.toString)
      println()
    }
    stats.getExecStats // return stats
  }

  def calcThroughput(time: Long, totalOps: Long): Long = {
    totalOps / time
  }

  /** */
  def linExecute(verbose: Boolean, name: String, cm: CaseManager, cr: CaseReasonerI, file: File, outfile: File, iterations: Int, OFF_SET: Int): StatsData = {

    require(iterations > OFF_SET, "OFF_SET must be less then iterations to prevent division by zero exception")

    val stats: Stats = new Stats(name, iterations, t)
    val stopper: StopWatch = new StopWatch
    val fw: FeatureWeights = cm.getDefaultFeatureWeights

    stopper.start()
    if (verbose) {
      println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
      println("Linear Benchmark for: " + name)
      println("Off-set: " + OFF_SET)
      println("Iterations: " + iterations)
      println("Time unit: " + t.toString)
      println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
      println("Start benchmarking...")
    }

    for (k <- 0 to iterations + OFF_SET) {

      if (k >= OFF_SET) {
        stats.start()
        // fetch a new list for each iteration
        val resList: CaseMap = cm.createNewCaseMap(100)


        for (i <- 0 to 1) {
          for (j <- MIN to MAX) {
            val refCase = cm.getCase(j) // 1
            resList.addCase(refCase)
            val simCase = cr.getMostSimilarCase(refCase, cm, fw)
            resList.addCase(simCase)
          }
        }
        stats.stop(k - OFF_SET)
      }
    }

    stopper.stop()
    if (verbose) {
      // stats.printCpuTime
      if (EXCL_STATS) {
        stats.safeAll(file)
      }

      stats.printExecutionTime()
      println("Done! Benchmarking execution time " + name + " took: " + stopper.getElapsedTime(ElapsedTime.EXEC_TIME, ST) + " " + ST.toString)
      println()
    }
    stats.getExecStats // return stats
  }

  def parExecute(verbose: Boolean, name: String, cm: CaseManager, cr: CaseReasonerI, statsFile: File, outfile: File, iterations: Int, OFF_SET: Int): StatsData = {

    require(iterations > OFF_SET, "OFF_SET must be less then iterations to prevent division by zero exception")
    val stats: Stats = new Stats(name, iterations, t)
    val stopper: StopWatch = new StopWatch
    val fw: FeatureWeights = cm.getDefaultFeatureWeights

    stopper.start()

    if (verbose) {
      println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
      println("Parallel Benchmark for: " + name)
      println("Off-set: " + OFF_SET)
      println("Iterations: " + iterations)
      println("Time unit: " + t.toString)
      println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
      println("Start benchmarking...")
    }

    for (k <- (0 until iterations + OFF_SET).par) yield {

      if (k >= OFF_SET) {
        stats.start()
        val resList = cm.createNewCaseMap(100)

        for (i <- 0 to 1) {

          for (j <- (MIN until MAX).par) yield {
            val refCase = cm.getCase(j)
            resList.addCase(refCase)
            val simCase = cr.getMostSimilarCase(refCase, cm, fw)
            resList.addCase(simCase)
          }

        }
        stats.stop(k - OFF_SET)
      }
    }
    stopper.stop()

    if (verbose) {
      // stats.printCpuTime
      if (EXCL_STATS) {
        stats.safeAll(statsFile)
      }
      stats.printExecutionTime()
      println("Done! Benchmarking execution time " + name + " took: " + stopper.getElapsedTime(ElapsedTime.EXEC_TIME, ST) + " " + ST.toString)
      println()
    }
    stats.getExecStats // return stats
  }

}
