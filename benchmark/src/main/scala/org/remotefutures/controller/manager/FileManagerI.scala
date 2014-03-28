/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.controller.manager

import org.remotefutures.model.CaseMap
import java.io.File

/**
 * @author Marvin Hansen
 */
trait FileManagerI {
  def write(caseMap: CaseMap, targetFile: File)

  def read(sourceFile: File): CaseMap
}