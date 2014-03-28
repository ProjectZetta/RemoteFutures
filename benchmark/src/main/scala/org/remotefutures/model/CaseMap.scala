/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.model

import java.util

/**
 * @author Marvin Hansen
 */
final case class CaseMap(map: util.Map[Int, Case]) {

  def createNewCaseMap(size: Int): CaseMap = new CaseMap(new java.util.HashMap[Int, Case](size))

  def addCase(c: Case) = map.put(c.journeyCode, c)

  def getCase(i: Int): Case = map.get(i)

  def removeCase(i: Int): Case = map.remove(i)

  def size(): Int = map.size()
}
