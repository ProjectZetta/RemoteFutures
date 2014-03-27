/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.controller.manager

import java.io._
import com.thoughtworks.xstream._
import com.thoughtworks.xstream.io.xml.DomDriver
import com.thoughtworks.xstream.io.StreamException
import com.thoughtworks.xstream.mapper.CannotResolveClassException
import org.remotefutures.model.{Hotel, Country, Case, CaseMap}
import org.remotefutures.utils.XStreamConversions
import org.remotefutures.model.enums.HolidayType.HolidayType
import org.remotefutures.model.enums.Transportation.Transportation
import org.remotefutures.model.Accommodation.Accommodation
import org.remotefutures.model.enums.Month.Month

/**
 * @author Marvin Hansen
 */

trait XmlFileMangerImpl extends FileManagerI {

  private final val xmlstream = createXmlXStream

  /** @param caseMap the case hashmap to save
    * @param targetFile the target statsFile */
  override def write(caseMap: CaseMap, targetFile: File) {

    val writer: Writer = new OutputStreamWriter(new FileOutputStream(targetFile))

    try {
      xmlstream.toXML(caseMap, writer)
    } catch {
      case ex: StreamException => throw new FileNotFoundException("Error: cannot write to file. Please check path to file: " + targetFile)
      case ex: FileNotFoundException => throw new FileNotFoundException("Error: cannot write to file. Please check path to file: " + targetFile)
      case e: Exception => throw new Exception(e)
    }

  }

  /** @param sourceFile the xml statsFile to load.
    * @return a Java Hashmap containing all cases */
  override def read(sourceFile: File): CaseMap = {

    try {
      xmlstream.fromXML(sourceFile).asInstanceOf[CaseMap]
    } catch {
      case ex: StreamException => throw new FileNotFoundException("Error: Missing file. Please check path to file: " + sourceFile)
      case ex: FileNotFoundException => throw new FileNotFoundException("Error: Missing file. Please check path to file: " + sourceFile)
      case ex: IOException => throw new IOException("Error: IO Exception: cannot load file from: " + sourceFile)
      case ex: CannotResolveClassException => throw new Exception("Don't understand input format. Please Check XML syntax of file: " + sourceFile)
      case e: Exception => throw new Exception(e)
    }
  }

  /** Private factory method that creates and configures an xstream
    * @return pre-configured xstream */
  private def createXmlXStream = {

    new XStreamException("Test")
    val xstream = XStreamConversions(new XStream(new DomDriver()))

    xstream.alias("CaseMap", classOf[CaseMap])
    xstream.alias("Case", classOf[Case])
    xstream.alias("HolidayType", classOf[HolidayType])
    xstream.alias("Country", classOf[Country])
    xstream.alias("Transportation", classOf[Transportation])
    xstream.alias("Month", classOf[Month])
    xstream.alias("Accommodation", classOf[Accommodation])
    xstream.alias("Hotel", classOf[Hotel])

    xstream
  }
}
