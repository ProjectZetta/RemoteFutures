/*
 * Copyright (c) 2014 Martin Senne, Marvin Hansen.
 */
package org.remotefutures.core.impl

import org.remotefutures.core.{Settings, RemoteExecutionContext}
import com.typesafe.config.Config
import scala.concurrent.Promise
import org.remotefutures.core.impl.local.LocalRunningRemoteExecutor

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

    // construction of the remote execution context by reflection
    instantiateByClassname( settings.RemoteExecutionContextClassname )(settings, reporter)
  }
}