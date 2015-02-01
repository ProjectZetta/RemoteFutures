package org.remotefutures.core

/**
 * The state monad.
 *
 * @tparam S is the type of the state
 * @tparam A is the type of the result
 */
trait State[S, A] {

  def run(initial: S) : (S, A)

  def eval(s: S): A = run(s)._2

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

  // same as unit in FP
  def state[S, A](a: A) : State[S, A] = {
    State {
      s => (s, a)
    }
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

  def set[S](s: S): State[S, Unit] = put(s)


  /**
   * Convenience function to modify the current state
   * @param f
   * @tparam S
   * @return
   */
  def modify[S](f: S => S): State[S, Unit] = {
    State {
      s: S => (f(s), ())
    }
  }


}

object FibViaStateMonad {
  def main(args: Array[String]) : Unit = {
    type Memo = Map[Int, Int]

    def meFib(n:Int) : State[Memo, Int] = {
      n match {
        case 0 => State.state(0)
        case 1 => State.state(1)
        case n =>
          println(s"Case $n")

          for {
          //Option[Int]    State[Memo, Option[Int]]
            memoed <- State.gets { m: Memo => m.get(n)}

            r <- memoed match {
              case Some(fibN) => {
                println(s"Retrieved from map: fib($n)=$fibN")
                State.state[Memo,Int](fibN)
              }
              case None => for {

                a <- meFib(n - 1)
                b <- meFib(n - 2)
                fibN = {
                  println(s"Calculated fib($n)");
                  a + b
                }
                _ <- State.modify { memo: Memo => {
                  val t = memo + (n -> fibN)
                  println("Memo: " + t)
                  t}
                }
              } yield fibN
            }
          } yield r
      }
    }

    def fib(n:Int) : Int = meFib(n).eval(Map.empty)

    println( fib(10) )
  }
}


object FibViaStateMonadFromTony {
  def main(args:Array[String]): Unit = {
    type Memo = Map[Int, Int]

    def fibmemoR(z: Int): State[Memo, Int] =
      if(z <= 1)
        State.state(z)
      else
        for {
          u <- State.gets((m: Memo) => m get z)
          // test: Option[State[Memo, Int]] = u.map(State.state[Memo, Int])
                       // (Int) => State[Memo, Int] =
            v <- u map State.state[Memo, Int] getOrElse (for {
              r <- fibmemoR(z - 1)
              s <- fibmemoR(z - 2)
              t = {
                println(s"Calculated fib($z)")
                r + s
              }
              _ <- State.modify((m: Memo) => {
                val r = m + ((z, t))
                println("Memo: " + r)
                r
              })
          } yield t)
        } yield v

    def fib(n:Int) : Int = fibmemoR(n).eval(Map.empty)

    println("Tony's version")

    println( fib(10) )
  }
}


