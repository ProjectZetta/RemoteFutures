package org.remotefutures.core

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

  def state[S, A](a: A) : State[S, A] = {
    State { s => (s, a) }
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



