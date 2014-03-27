/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.utils

import java.util.concurrent.TimeUnit
import management.{ManagementFactory, ThreadMXBean}
import java.util.concurrent.TimeUnit._

/**
 * @author Marvin Hansen
 *         Stop watch counts execution time of a task
 */
class StopWatch extends StopWatchI with StopWatchImpl

object StopWatch extends StopWatch

/** Stopwatch Interface */
protected abstract class StopWatchI {

  import ElapsedTime._

  def start()

  def stop()

  def getElapsedTime(e: ElapsedTime, t: TimeUnit): Double
}

/** Stopwatch Implementation *//** Stopwatch Implementation */
protected trait StopWatchImpl extends StopWatchI {


  import ElapsedTime._

  private val mx: ThreadMXBean = ManagementFactory.getThreadMXBean
  private final val Micro_Divisor: Double = 1E3
  private final val Milli_Divisor: Double = 1E6
  private final val Sec_Divisor: Double = 1E9
  private final val _NULL: Long = 0L

  private var start_Time: Long = _NULL
  private var start_Cpu_Time: Long = _NULL
  private var start_User_Time: Long = _NULL
  private var stop_Time: Long = _NULL
  private var stop_Cpu_Time: Long = _NULL
  private var stop_User_Time: Long = _NULL
  private var running: Boolean = false

  override def start() {
    this.start_Time = System.nanoTime
    this.start_Cpu_Time = mx.getCurrentThreadCpuTime
    this.start_User_Time = mx.getCurrentThreadUserTime
    this.running = true
  }

  override def stop() {
    this.stop_Time = System.nanoTime
    this.stop_Cpu_Time = mx.getCurrentThreadCpuTime
    this.stop_User_Time = mx.getCurrentThreadUserTime
    this.running = false
  }

  /** @return elapsed time */
  override def getElapsedTime(e: ElapsedTime, t: TimeUnit): Double = t match {
    case NANOSECONDS => getElapsedTime(e)
    case MICROSECONDS => getElapsedTime(e) / Micro_Divisor
    case MILLISECONDS => getElapsedTime(e) / Milli_Divisor
    case SECONDS => getElapsedTime(e) / Sec_Divisor
    case MINUTES => TimeUnit.MINUTES.convert(getElapsedTime(e), TimeUnit.NANOSECONDS) // implies a certain loss of
    case HOURS => TimeUnit.HOURS.convert(getElapsedTime(e), TimeUnit.NANOSECONDS) // precision but that's okay
    case DAYS => TimeUnit.DAYS.convert(getElapsedTime(e), TimeUnit.NANOSECONDS) // considering ns -> days...
  }

  private def getElapsedTime(t: ElapsedTime): Long = t match {
    case EXEC_TIME => getExecutionTime
    case CPU_TIME => getCpuTime
    case USER_TIME => getUserTime
  }

  /** @return elapsed time in nanoseconds */
  private def getExecutionTime: Long = {
    if (running) System.nanoTime() - start_Time
    else stop_Time - start_Time
  }

  /** @return elapsed CPU time in nanoseconds */
  private def getCpuTime: Long = {
    if (running) mx.getCurrentThreadCpuTime - start_Cpu_Time
    else stop_Cpu_Time - start_Cpu_Time
  }

  /** @return elapsed user time in nanoseconds */
  private def getUserTime: Long = {
    if (running) mx.getCurrentThreadUserTime - start_User_Time
    else stop_User_Time - start_User_Time
  }

}