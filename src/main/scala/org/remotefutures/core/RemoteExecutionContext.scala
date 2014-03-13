/*
 * Copyright (c) 2014 Martin Senne, Marvin Hansen.
 */
package org.remotefutures.core

import scala.concurrent.{ExecutionContextExecutor}

/**
 * A Remote
 */
trait RemoteExecutionContext {

  /**
   * Execute a function in its given context on a distant place.
   *
   * @param body is the code to execute and return T eventually
   * @param bodyContext is the context/closure of of function body: () => T
   * @tparam C specifies the Context type
   * @tparam T specifies the return tyoe
   */
  def execute[C, T](body: () => T, bodyContext: C): Unit

  /**
   * Reports that an asynchronous computation failed.
   */
  def reportFailure(cause: Throwable): Unit

  /**
   * Prepares for the execution of a task. Returns the prepared
   * execution context. A valid implementation of `prepare` is one
   * that simply returns `this`.
   */
  def prepare(): RemoteExecutionContext = this

}

object RemoteExecutionContext {

  /**
   * This is the explicit global RemoteExecutionContext,
   * call this when you want to provide the global ExecutionContext explicitly
   */
  def global: RemoteExecutionContext = Implicits.global

  object Implicits {
    /**
     * This is the implicit global RemoteExecutionContext,
     * import this when you want to provide the global ExecutionContext implicitly
     */
    implicit lazy val global: RemoteExecutionContext = impl.RemoteExecutionContextImpl.fromRemoteExecutor(null: RemoteExecutor)
  }

  /**
   * Creates an `ExecutionContext` from the given `Executor`.
   */
  def fromRemoteExecutor(e: RemoteExecutor, reporter: Throwable => Unit): RemoteExecutionContext = {
    impl.RemoteExecutionContextImpl.fromRemoteExecutor(e, reporter)
  }

  /** Creates an `ExecutionContext` from the given `Executor` with the default Reporter. */
  def fromRemoteExecutor(e: RemoteExecutor): RemoteExecutionContext = fromRemoteExecutor(e, defaultReporter)

  /** The default reporter simply prints the stack trace of the `Throwable` to System.err. */
  def defaultReporter: Throwable => Unit = _.printStackTrace()
}


