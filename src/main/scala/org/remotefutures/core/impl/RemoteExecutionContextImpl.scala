/*
 * Copyright (c) 2014 Martin Senne
 */
package org.remotefutures.core.impl

import org.remotefutures.core.{Settings, RemoteExecutionContext}
import com.typesafe.config.Config

/**
 *
 */
private[core] object RemoteExecutionContextImpl {
  def fromConfig( c: Config, reporter: Throwable => Unit = RemoteExecutionContext.defaultReporter): RemoteExecutionContext = {

    def instantiateByClassname[T](fqn: String)(args:AnyRef*): T = {
      val clazz = Class.forName(fqn)
      val constructor = clazz.getConstructors()(0)
      println( "Instantiated class is " + clazz)
      println("Constructor is " + constructor)
      println(args)
      constructor.newInstance(args:_*).asInstanceOf[T]
    }

    val settings = Settings(c)

    /**
     * Constructed remote execution context via reflection and
     * the classname given in [[settings.RemoteExecutionContextClassname]]
     */
    val rec: RemoteExecutionContext = {
      instantiateByClassname[RemoteExecutionContext](settings.RemoteExecutionContextClassname)(settings, reporter)
    }
    rec
  }
}