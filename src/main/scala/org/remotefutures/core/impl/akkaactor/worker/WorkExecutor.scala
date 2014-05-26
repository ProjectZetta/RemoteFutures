package org.remotefutures.core.impl.akkaactor.worker

import akka.actor.Actor

// I don't get this one...
// Presumably, this is a proof of concept for an executor.
// I assume an actor for generic code execution,
// similar to an executorService would be required to
// get arbitrary code executed.
//
// However, do we get a typing issue here?
// Just asking because one could make
// receive {
// case x : Any
// which nullifies type safety altogether
// or
// use a compiler macro with an actor template to generate
// specific, fully typed, worker actors.
// the later is how pickler generates type specific serializers
//
class WorkExecutor extends Actor {

  def receive = {
    case n: Int =>
      val n2 = n * n
      val result = s"$n * $n = $n2"
      sender ! Worker.WorkComplete(result)
  }
}