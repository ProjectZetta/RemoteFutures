/*
 * Copyright (c) 2014 Martin Senne, Marvin Hansen.
*/
package org.remotefutures.utils


import jxl.write.Formula
import jxl.write.Label
import _root_.jxl.write.NumberFormats
import _root_.jxl.write.WritableCellFormat
import _root_.jxl.write.WritableSheet
import _root_.jxl.write.WritableWorkbook
import jxl.{WorkbookSettings, Workbook}
import java.io.{IOException, File}
import jxl.write.Number
import java.util.Locale

/**
 * @author Marvin Hansen
 */
object ExcelWriter extends ExcelWriter

class ExcelWriter {

  private var idx_sheet = 0
  private val local: Locale = new Locale.Builder().setLanguage("en").setRegion("US").build()

  def writeAllStats(file: File, args: List[StatsData]) {
    val workbook: WritableWorkbook = createNewWritableWorkbook(file)

    writeSummary(workbook, args)


    for (v <- args) {
      writeStats(workbook, v)
    }

    writeWorkBook(workbook)

  }

  /**
   * @param workbook the workbook to write in
   * @param args one or many stats that needs to be summarized
   */
  private def writeSummary(workbook: WritableWorkbook, args: List[StatsData]) {
    val sheet: WritableSheet = createNewWorkSheet("Summary", workbook)

    var number: Number = null
    var col: Int = 0
    var label: Label = new Label(col, 2, "Time")

    sheet.addCell(label)
    label = new Label(col, 3, "Min")
    sheet.addCell(label)
    label = new Label(col, 4, "Max")
    sheet.addCell(label)
    label = new Label(col, 5, "Mean value")
    sheet.addCell(label)
    label = new Label(col, 6, "Variance ")
    sheet.addCell(label)
    label = new Label(col, 7, "Standard derivation")
    sheet.addCell(label)
    col = 1
    for (v <- args) {
      label = new Label(col, 0, v.name)
      sheet.addCell(label)
      number = new Number(col, 3, v.min)
      sheet.addCell(number)
      number = new Number(col, 4, v.max)
      sheet.addCell(number)
      number = new Number(col, 5, v.mean)
      sheet.addCell(number)
      number = new Number(col, 6, v.variance)
      sheet.addCell(number)
      number = new Number(col, 7, v.stdDev)
      sheet.addCell(number)
      col += 1
    }
  }

  /**
   * @param workbook the workbook to safe data
   * @param data statistical data to write into the workbook
   */
  private def writeStats(workbook: WritableWorkbook, data: StatsData) {

    val name: String = data.name
    val min: Double = data.min
    val max: Double = data.max
    val mean: Double = data.mean
    val variance: Double = data.variance
    val stdDev: Double = data.stdDev
    val sheet: WritableSheet = createNewWorkSheet(name, workbook)
    val cf: WritableCellFormat = {
      new WritableCellFormat(NumberFormats.DEFAULT)
    }

    var buf: StringBuffer = new StringBuffer
    var number: Number = null

    var col: Int = 0
    var row: Int = 0


    for (d <- data.data_arr) {
      number = new Number(col, row, d)
      sheet.addCell(number)
      row += 1

    }

    val l1_calculated: Label = new Label(1, 1, "Calculated")
    sheet.addCell(l1_calculated)
    val l2_Measured: Label = new Label(1, 2, "Measured")
    sheet.addCell(l2_Measured)
    val l3_Mean: Label = new Label(2, 0, "Mean")
    sheet.addCell(l3_Mean)
    buf.append("AVERAGE(A1:A101)")
    var f: Formula = new Formula(2, 1, buf.toString)
    sheet.addCell(f)
    number = new Number(2, 2, mean, cf)
    sheet.addCell(number)
    val l4_3_Mean: Label = new Label(3, 0, " 3 x Mean")
    sheet.addCell(l4_3_Mean)
    buf = new StringBuffer
    buf.append("3 * C2")
    f = new Formula(3, 1, buf.toString)
    sheet.addCell(f)
    number = new Number(3, 2, 3 * mean, cf)
    sheet.addCell(number)
    val l5_min: Label = new Label(4, 0, "Min")
    sheet.addCell(l5_min)
    buf = new StringBuffer
    buf.append("MIN(A1:A101)")
    f = new Formula(4, 1, buf.toString)
    sheet.addCell(f)
    number = new Number(4, 2, min, cf)
    sheet.addCell(number)
    val l6_max: Label = new Label(5, 0, "Max")
    sheet.addCell(l6_max)
    buf = new StringBuffer
    buf.append("MAX(A1:A101)")
    f = new Formula(5, 1, buf.toString)
    sheet.addCell(f)
    number = new Number(5, 2, max, cf)
    sheet.addCell(number)
    val l7_var: Label = new Label(6, 0, "Variance")
    sheet.addCell(l7_var)
    buf = new StringBuffer
    buf.append("VAR(A1:A101)")
    f = new Formula(6, 1, buf.toString)
    sheet.addCell(f)
    number = new Number(6, 2, variance, cf)
    sheet.addCell(number)
    val l8_std_dev: Label = new Label(7, 0, "Std Dev")
    sheet.addCell(l8_std_dev)
    buf = new StringBuffer
    buf.append("VAR(A1:A101)")
    f = new Formula(7, 1, buf.toString)
    sheet.addCell(f)
    number = new Number(7, 2, stdDev, cf)
    sheet.addCell(number)
  }

  /**
   * @param file a statsFile to safe the workbook
   * @return a new
   */
  private def createNewWritableWorkbook(file: File): WritableWorkbook = {
    idx_sheet = 0
    createWorkBook(file)
  }

  /**
   * @param name name of the worksheet
   * @param workbook the workbook to which the sheet shall be added
   * @return a new worksheet
   */
  private def createNewWorkSheet(name: String, workbook: WritableWorkbook): WritableSheet = {
    val sheet: WritableSheet = workbook.createSheet(name, idx_sheet)
    idx_sheet += 1
    sheet
  }

  /**
   * @param file location to safe the workbook
   * @return a newly created excel workbook
   */
  private def createWorkBook(file: File): WritableWorkbook = {
    val settings = new WorkbookSettings

    settings.setLocale(local)
    var workbook: WritableWorkbook = null

    try {
      workbook = Workbook.createWorkbook(file, settings)
    }
    catch {
      case e: IOException =>
        throw new RuntimeException("This should never happen, I know this statsFile exists", e)
    }
    workbook
  }

  /** @param workbook to safe */
  private def writeWorkBook(workbook: WritableWorkbook) {
    try {
      workbook.write()
      workbook.close()
    }
    catch {
      case e: Any =>
        throw new RuntimeException("This should never happen, I know this statsFile can be written", e)
    }
  }

}