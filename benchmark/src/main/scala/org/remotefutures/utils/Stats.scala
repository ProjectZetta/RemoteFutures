/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.utils

import java.util.concurrent.TimeUnit
import Numeric._
import java.io.File

/**
 * @author Marvin Hansen
 */
class Stats(name: String, iterations: Int, timeUnit: TimeUnit) {

  private final val stopper: StopWatch = new StopWatch
  private final val writer: ExcelWriter = new ExcelWriter
  private final val exec_time_arr: Array[Double] = new Array[Double](iterations + 1)
  private final val cpu_time_arr: Array[Double] = new Array[Double](iterations + 1)
  private final val user_time_arr: Array[Double] = new Array[Double](iterations + 1)

  /** starts the stop watch   */
  def start() {
    stopper.start()
  }

  /** stops the stopwatch for iteration i
    *
    * @param i iteration
    */
  def stop(i: Int) {
    stopper.stop()

    exec_time_arr(i) = stopper.getElapsedTime(ElapsedTime.EXEC_TIME, timeUnit)
    cpu_time_arr(i) = stopper.getElapsedTime(ElapsedTime.CPU_TIME, timeUnit)
    user_time_arr(i) = stopper.getElapsedTime(ElapsedTime.USER_TIME, timeUnit)
  }


  /** saves all data into one excel statsFile
    * @param file statsFile to save the excel sheet */
  def safeAll(file: File) {

    val exec: StatsData = getAllStatsData(name + "_Execution_Time", timeUnit, exec_time_arr)
    val cpu: StatsData = getAllStatsData(name + "_CPU_Time", timeUnit, cpu_time_arr)
    writeStats2ExcelFile(file, exec, cpu)
  }

  def getExecStats: StatsData = getAllStatsData(name + "_Execution_Time", timeUnit, exec_time_arr)

  def getCPUStats: StatsData = getAllStatsData(name + "_CPU_Time", timeUnit, cpu_time_arr)


  /** @param name name of the experiment
    * @param timeUnit  time unit
    * @param arr data array
    * @return a new statsData case class containing a statistical evaluation of the raw data */
  private def getAllStatsData(name: String, timeUnit: TimeUnit, arr: Array[Double]): StatsData = createNewStatsData(name, timeUnit, arr)


  def printExecutionTime() {
    printStats(name, "Execution Time", timeUnit, iterations, exec_time_arr)
  }

  def printPlainExecutionTimeData() {
    printPlainData("Execution time", exec_time_arr)
  }

  def printExecutionTimeArray() {
    printArray("Execution time", exec_time_arr)
  }

  def printCpuTime() {
    printStats(name, "CPU Time", timeUnit, iterations, cpu_time_arr)
  }

  def printPlainCpuTimeData() {
    printPlainData("CPU Time", cpu_time_arr)
  }

  def printCpuTimeData() {
    printArray("CPU Time", cpu_time_arr)
  }

  def printUserTime() {
    printStats(name, "User Time", timeUnit, iterations, user_time_arr)
  }

  def printPlainUserTimeData() {
    printPlainData("User Time", user_time_arr)
  }

  def printUserTimeData() {
    printArray("User Time", user_time_arr)
  }

  /** @param file to save excel statsFile
    * @param args one or many statistics to write into an excel statsFile */
  private def writeStats2ExcelFile(file: File, args: StatsData*) {

    writer.writeAllStats(file, args.toList)
  }

  /** @param name name of the experiment
    * @param timeUnit  time unit
    * @param arr data array
    * @return a new statsData case class containing a statistical evaluation of the raw data */
  private def createNewStatsData(name: String, timeUnit: TimeUnit, arr: Array[Double]): StatsData = {

    new StatsData(name, timeUnit, arr, min(arr), max(arr), mean(arr), variance(arr), standardDeviation(arr))
  }

  /** @param name name of the experiment
    * @param timeMeasurement what has been measured?
    * @param timeUnit time unit of the data
    * @param iterations number of iterations
    * @param arr data array  */
  private def printStats(name: String, timeMeasurement: String, timeUnit: TimeUnit, iterations: Int, arr: Array[Double]) {

    println()
    println("Measurement results for " + name)
    println()
    println(timeMeasurement + " for " + iterations + " iterations" + " in " + timeUnit)
    println()
    println("Min " + timeMeasurement + ",   " + min(arr))
    println("Max " + timeMeasurement + ",   " + max(arr))
    println("Mean " + timeMeasurement + ",  " + mean(arr))
    println("Variance " + timeMeasurement + ", " + variance(arr))
    println("Standard derivation " + timeMeasurement + ", " + standardDeviation(arr))
    println()
  }

  /** @param timeMeasurement what does the raw data show?
    * @param arr Array to print raw data from  */
  private def printPlainData(timeMeasurement: String, arr: Array[Double]) {

    println()
    println("Plain data for: " + timeMeasurement)
    println()
    println(min(arr))
    println(max(arr))
    println(mean(arr))
    println(variance(arr))
    println(standardDeviation(arr))
    println()
  }

  /** @param arrayName Name of the array to print
    * @param arr Array to print */
  private def printArray(arrayName: String, arr: Array[Double]) {

    for (a <- arr) {
      println(a)
    }
  }

  /** @param values array
    * @return variance  */
  private def variance(values: Array[Double]): Double = {

    values.map(value => square(value - mean(values))).sum / iterations
  }

  /** @param values array
    * @return max value of the array */
  private def max(values: Array[Double]): Double = values.max

  /** @param values array
    * @return min value of the array  */
  private def min(values: Array[Double]): Double = values.min

  /** @param values array
    * @return standard derivation  */
  private def standardDeviation(values: Array[Double]): Double = scala.math.sqrt(variance(values))

  /** @param values the values for mean calculation
    * @return mean value   */
  private def mean(values: Array[Double]): Double = values.sum / iterations

  /** @param x the number to square
    * @return squared value   */
  private def square(x: Double): Double = x * x
}

