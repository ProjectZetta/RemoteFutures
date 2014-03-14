/*
 * Copyright (c) 2014 Martin Senne, Marvin Hansen.
 */
package org.remotefutures.core.impl

import org.remotefutures.core.{RemoteExecutor, Settings, RemoteExecutionContext}
import com.typesafe.config.Config
import org.remotefutures.core.impl.executor.LocalRunnableRemoteExecutor
import scala.concurrent.Promise

// private[core] class DummyRemoteExecutionContext private[impl] (settings : Settings, reporter: Throwable => Unit) extends RemoteExecutionContext {
class DummyRemoteExecutionContext(settings : Settings, reporter: Throwable => Unit) extends RemoteExecutionContext {

  /**
   * Facility to create a RemoteExecutor used in the context
   * in case none is given. The actual point is providing
   * some kind of near-zero overhead default RemoteExecutor
   *
   * @return RemoteExecutor
   */
  val executor: RemoteExecutor = new LocalRunnableRemoteExecutor

  override def execute[C, T](body: () => T, bodyContext: C, promise: Promise[T]): Unit = {
    // call should be something like executor.execute(body,bodyContext)
    // This goes hand in hand with the interface definition of RemoteExecutor
    // already suggested.
    executor.execute(body, bodyContext, promise)
  }

  override def reportFailure(t: Throwable) = reporter(t)

  override def shutdown(): Unit = ???

  override def startup(): Unit = ???
}

private[core] object RemoteExecutionContextImpl {
  def fromConfig( c: Config, reporter: Throwable => Unit = RemoteExecutionContext.defaultReporter): RemoteExecutionContext = {

    def instantiateByClass[T](clazz: java.lang.Class[T], args:AnyRef*): T = {
      val constructor = clazz.getConstructors()(0)
      println("Constructor is " + constructor)
      println(args)
      constructor.newInstance(args:_*).asInstanceOf[T]
    }

    def instantiateByClassname[T](fqn: String)(args:AnyRef*): T = {
      val clazz = Class.forName(fqn)
      val constructor = clazz.getConstructors()(0)
      println("Constructor is " + constructor)
      println(args)
      constructor.newInstance(args:_*).asInstanceOf[T]
    }

    val settings = Settings(c)

    instantiateByClassname("org.remotefutures.core.impl.DummyRemoteExecutionContext")(settings, reporter)
  }
}