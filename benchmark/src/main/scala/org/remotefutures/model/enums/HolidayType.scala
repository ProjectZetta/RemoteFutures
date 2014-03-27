/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.model.enums

/**
 * @author Marvin Hansen
 */
object HolidayType extends Enumeration {
  type HolidayType = Value
  val ACTIVE = Value(1)
  val BATHING = Value(2)
  val CITY = Value(3)
  val EDUCATION = Value(4)
  val LANGUAGE = Value(5)
  val RECREATION = Value(6)
  val SKIING = Value(7)
  val WANDERING = Value(8)
}
