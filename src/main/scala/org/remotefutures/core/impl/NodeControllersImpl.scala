/*
 * Copyright (c) 2014 Martin Senne
 */
package org.remotefutures.core.impl

import org.remotefutures.core.{NodeControllers, Settings, RemoteExecutionContext}
import com.typesafe.config.Config

/**
 *
 */
private[core] object NodeControllersImpl {
  def fromConfig( config: Config ): NodeControllers = {

    def instantiateByClassname[T](fqn: String)(args:AnyRef*): T = {
      val clazz = Class.forName(fqn)
      val constructor = clazz.getConstructors()(0)
      println( "Instantiated class is " + clazz)
      println("Constructor is " + constructor)
      println(args)
      constructor.newInstance(args:_*).asInstanceOf[T]
    }

    val settings = Settings( config )

    /**
     * Constructed remote execution context via reflection and
     * the classname given in [[settings.NodeControllersFQCN]]
     */
    val nc: NodeControllers = {
      instantiateByClassname[NodeControllers](settings.NodeControllersFQCN)( settings )
    }
    nc
  }
}