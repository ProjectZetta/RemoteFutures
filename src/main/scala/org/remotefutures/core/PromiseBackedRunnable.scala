/*
 * Copyright (c) 2014 Martin Senne, Marvin Hansen.
 */
package org.remotefutures.core

import scala.concurrent.Promise

/**
 * A runnable which holds a promise, which is completed / written to from within run().
 * @tparam T is the type of the promise.
 */
abstract class PromiseBackedRunnable[T] extends Runnable {

  /**
   * The promise that is written to in order to return back a value
   */
  val promise : Promise[T]
}
