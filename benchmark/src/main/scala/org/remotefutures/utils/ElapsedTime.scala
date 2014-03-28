/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.utils

/**
 * @author Marvin Hansen
 *
 *         Enumeration that defined what kind of elapsed time can be obtained
 */
object ElapsedTime extends Enumeration {
  type ElapsedTime = Value
  val EXEC_TIME, CPU_TIME, USER_TIME = Value
}