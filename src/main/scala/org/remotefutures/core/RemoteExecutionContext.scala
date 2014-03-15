/*
 * Copyright (c) 2014 Martin Senne, Marvin Hansen.
 */
package org.remotefutures.core

import scala.concurrent.Promise
import com.typesafe.config.{ConfigFactory, Config}

/**
 * Remote execution context provides methods to setup the execution environment.
 */
trait RemoteExecutionContext {

  /**
   * Execute a function in its given context on a distant place.
   *
   * @param body is the code to execute and return T eventually
   * @param bodyContext is the context/closure of of function fnc: () => T
   * @tparam C specifies the Context type
   * @tparam T specifies the return tyoe
   */
  def execute[C, T](body: () => T, bodyContext: C, promise: Promise[T]): Unit

  /**
   * Reports that an asynchronous computation failed.
   */
  def reportFailure(cause: Throwable): Unit

  def startup(): Unit

  def shutdown(): Unit

}

object RemoteExecutionContext {

  /**
   * This is the explicit global RemoteExecutionContext,
   * call this when you want to provide the global ExecutionContext explicitly
   */
  def global: RemoteExecutionContext = Implicits.default

  object Implicits {
    /**
     * This is the implicit global RemoteExecutionContext,
     * import this when you want to provide the global ExecutionContext implicitly
     */
    implicit lazy val default: RemoteExecutionContext = RemoteExecutionContext.fromDefaultConfig
  }

  def fromConfig(c: Config): RemoteExecutionContext = impl.RemoteExecutionContextImpl.fromConfig(c)

  def fromDefaultConfig: RemoteExecutionContext = {
    val c = ConfigFactory.load("remotefutures")
    fromConfig(c)
  }

  /**
   * The default reporter simply prints the stack trace of the `Throwable` to System.err.
   */
  def defaultReporter: Throwable => Unit = _.printStackTrace()
}


