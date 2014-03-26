/*
 * Copyright (c) 2014 Martin Senne, Marvin Hansen.
 */
package org.remotefutures.core

import scala.concurrent.Future

/**
 * A remote future executes code, given as body, asynchronously on a distant remote node.
 */
object RemoteFuture {
  def apply[T](body: => T)(implicit res: RemoteExecutionContext): Future[T] = impl.RemoteFutureImpl[T](body)
}