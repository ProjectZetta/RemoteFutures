/*
 * Copyright (c) 2014 Martin Senne, Marvin Hansen.
 */
package org.remotefutures.core

import scala.concurrent.Promise

/**
 * A idea_runnable which allows access to the promise, which is completed / written to from within run().
 */
abstract class PromiseBackedRunnable[T] extends Runnable {
  val promise : Promise[T]
}
