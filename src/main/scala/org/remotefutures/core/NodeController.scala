/*
* Copyright (c) 2014 Martin Senne.
*/
package org.remotefutures.core

trait NodeType

sealed trait NodeState
case object NodeDown extends NodeState
case object NodeUp extends NodeState

trait NodeInformation[S]


class ToConcreteType[S, SUB] {
  def convert( generic: S ): SUB = {
    generic.asInstanceOf[SUB]
  }
}

trait NodeControllers {

  def nodeController( nodeType : NodeType ) : NodeController

  def specificNodeController[C](nodeType: NodeType)(implicit toConcrete: ToConcreteType[NodeController, C]): C

  def nodeTypes : Set[NodeType]
}


/**
 * An abstract node controller.
 */
trait NodeController {

  type N <: NodeType

  type S <: NodeInformation[N]

  def start( port: Int ) : S

  def stop : Unit
}
//
//object EmptyController
//  extends NodeController {
//  // override type S = this.type
//  // type N = this.type
//
//  override def stop: Unit = {} // do nothing
//
//  override def start(port: Int): EmptyController.S = {
//    null
//  }
//}



/**
 * The state monad.
 *
 * @tparam S is the type of the state
 * @tparam A is the type of the result
 */
trait State[S, +A] {
  def run(initial: S) : (S, A)
  def map[B](f: A => B): State[S, B] = {
    State { s =>
      val (s1, a) = run(s)
      (s1, f(a))
    }
  }
  def flatMap[B](f: A => State[S,B]): State[S, B] = {
    State { s =>
      val (s1, a) = run(s)
      f(a).run(s1)
    }
  }
}

object State {
  def apply[S, A](f: S => (S,A)): State[S, A] = {
    new State[S, A] {
      override def run(initial: S): (S, A) = f(initial)
    }
  }

  def state[S, A](a: A) : State[S,A] = {
    State { s => (s, a)}
  }

  def get[S] : State[S, S] = {
    State {
      s => (s, s)
    }
  }

  def gets[S, A](f: S => A): State[S, A] = {
    State {
      s => (s, f(s))
    }
  }

  def put[S](s: S): State[S, Unit] = {
    State {
      _ => (s, ())
    }
  }
}



