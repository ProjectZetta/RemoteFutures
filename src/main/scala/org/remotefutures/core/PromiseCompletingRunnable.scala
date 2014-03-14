/*
 * Copyright (c) 2014 Martin Senne, Marvin Hansen.
 */
package org.remotefutures.core

import scala.util.{Failure, Success}
import scala.util.control.NonFatal

/**
  */


/**
 * Wraps arbitrary code in a Future
 *
 * @param body code to execute
 * @tparam T return type of the code
 */
protected class PromiseCompletingRunnable[T](body: => T) extends Runnable with Serializable {
  final val promise = concurrent.Promise[T]()

  override def run() = {
    promise complete {
      try Success(body) catch {
        case NonFatal(e) => Failure(e)
      }
    }
  }
}
