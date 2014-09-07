/*
* Copyright (c) 2014 Martin Senne.
*/
package org.remotefutures.core

trait NodeType

sealed trait NodeState
case object NodeDown extends NodeState
case object NodeUp extends NodeState

trait NodeInformation[S]

/**
 * An abstract node controller.
 */
trait NodeController {

  type N <: NodeType

  type S <: NodeInformation[N]

  def start( port: Int ) : S

  def stop : Unit

  def executionContext(init: S) : Option[RemoteExecutionContext]
}
