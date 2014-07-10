/*
 * Copyright (c) 2014 Martin Senne, Marvin Hansen.
 */
package org.remotefutures.core

import scala.concurrent.Future
import org.remotefutures.spores._

/**
 * A remote future executes code, given as body, asynchronously on a distant remote node.
 * Still, it behaves like a regular [[scala.concurrent.Future]].
 */
object RemoteFuture {
  def apply[T](body: => T)(implicit res: RemoteExecutionContext): Future[T] = impl.RemoteFutureImpl[T](body)

  def apply[T](spore: NullarySpore[T])(implicit res: RemoteExecutionContext): Future[T] = impl.RemoteFutureImpl[T](spore)
}