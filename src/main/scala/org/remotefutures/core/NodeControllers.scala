package org.remotefutures.core

import com.typesafe.config.{ConfigFactory, Config}


trait NodeControllers {

  def apply( nodeTypeDesc: String ) : Option[NodeController]

  def apply( nodeType : NodeType ) : NodeController
}

/**
 * Node controllers to startup / shutdown nodes of different type.
 */
object NodeControllers {
  def fromConfig(c: Config) : NodeControllers = impl.NodeControllersImpl.fromConfig(c)

  def fromDefaultConfig : NodeControllers = {
    fromConfig( ConfigFactory.load("remotefutures"))
  }
}