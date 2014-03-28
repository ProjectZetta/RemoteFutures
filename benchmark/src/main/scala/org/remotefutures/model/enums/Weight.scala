/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.model.enums

/**
 * @author Marvin Hansen
 */
object Weight extends Enumeration {
  type Weight = Value
  val LOW = Value(1)
  val NORMAL = Value(3)
  val HIGH = Value(5)
}
