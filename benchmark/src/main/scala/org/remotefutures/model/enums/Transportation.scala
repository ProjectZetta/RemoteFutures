/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.model.enums

/**
 * @author Marvin Hansen
 */
object Transportation extends Enumeration {
  type Transportation = Value
  val CAR = Value(1)
  val COACH = Value(2)
  val PLANE = Value(3)
  val TRAIN = Value(4)
}
