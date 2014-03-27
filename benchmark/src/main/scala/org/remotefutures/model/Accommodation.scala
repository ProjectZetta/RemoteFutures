/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.model

/**
 * @author Marvin Hansen
 */
object Accommodation extends Enumeration {
  type Accommodation = Value
  val HOLIDAYFLAT = Value(1)
  val ONESTAR = Value(2)
  val TWOSTARS = Value(3)
  val THREESTARS = Value(4)
  val FOURSTARS = Value(5)
  val FIVESTARS = Value(6)
}
