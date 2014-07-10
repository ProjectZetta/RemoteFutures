/*
 * Copyright (c) 2014 Martin Senne
 */
package org.remotefutures.core.impl

import org.remotefutures.core.RemoteExecutionContext
import org.remotefutures.spores.NullarySpore
import scala.concurrent.{Promise, Future}

/**
 * A remote provides a simple facility to execute arbitrary code
 * on a remote host by using a remote executor. The result of
 * a Remote is wrapped into a Future thus
 * can be freely composed and combined with other Futures.
 *
 * "Modification is undesirable, but modifiability is paramount"
 * --Paul Phillips
 */
object RemoteFutureImpl {

  /**
   * Create a Future,such that the body is executed by the given remote execution context.
   *
   * @param body Code to execute
   * @param rec: RemoteExecutionContext used for execution of body
   * @tparam T result type of the executed code
   * @return the eventual result of the remote computation.
   */
  def apply[T](body: => T)(implicit rec: RemoteExecutionContext): Future[T] = {
    val p = Promise[T]()
    rec.execute(() => body, null, p)
    p.future
  }

  /**
   * Create a Future,such that the spore is executed by the given remote execution context.
   * A nullary spore is a [[Function0]] ( () => T ) plus that it captures variables used within the function.
   *
   * @param spore
   * @param rec: RemoteExecutionContext used for execution of body
   * @tparam T result type of the executed code
   * @return the eventual result of the remote computation.
   */
  def apply[T](spore: NullarySpore[T])(implicit rec: RemoteExecutionContext): Future[T] = {
    println("apply(spore) in RemoteFutureImpl.")
    val p = Promise[T]()
    rec.execute(spore, null, p)
    p.future
  }
}