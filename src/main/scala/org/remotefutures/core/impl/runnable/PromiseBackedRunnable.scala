/*
 * Copyright (c) 2014 Martin Senne, Marvin Hansen.
 */
package org.remotefutures.core.impl.runnable

import scala.concurrent.Promise

/**
 * A runnable which allows access to the promise, which is completed / written to from within run().
 */
abstract class PromiseBackedRunnable[T] extends Runnable {
  val promise : Promise[T]
}
