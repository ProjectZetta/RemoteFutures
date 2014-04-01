/*
 * Copyright (c) 2014 Marvin Hansen.
*/


import org.remotefutures.utils.Locals._
import org.remotefutures.utils.{ExcelWriter, Stats}
import org.scalatest.FlatSpec

import java.io.File
import java.util.concurrent.TimeUnit
import scala.util.Random


/**
 * @author Marvin Hansen
 */
class ExcelSpec extends FlatSpec {

  println("Checking locals")
  ShowLanguageAndLocale()
  println("done")

  final val name = "Test"
  final val t = TimeUnit.SECONDS
  final val file: File = new File("benchmark/Results/test.xls")
  final val arr = getArray
  final val s: Stats = new Stats(name, 5, t)
  final val writer: ExcelWriter = new ExcelWriter

  println("ExcelTest ")
  val exec = s.getAllStatsData(name + "_Execution_Time", t, arr)
  val cpu = s.getAllStatsData(name + "_CPU_Time", t, arr)
  val args = List(exec, cpu)

  "A data array" should "be not null " in {
    assert(exec != null)
    assert(cpu != null)
    assert(args != null)
  }

  println("Writing excel sheet ")
  writer.writeAllStats(file, args)


  def getArray: Array[Double] = {

    val array: Array[Double] = new Array[Double](51)
    val r = new Random(42)
    for {
      i <- array.indices
    } array(i) = r.nextDouble()
    array
  }

}
