/*
 * Copyright (c) 2014 Martin Senne
 */
package org.remotefutures.core.impl.akka.pullingworker

import akka.actor.{ActorLogging, Actor}

object WorkExecutor {
  case class WorkComplete(result: Any)
}

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
class WorkExecutor extends Actor with ActorLogging{

  def receive = {
    case body: (() => Any) ⇒

      // use () to actually execute the job
      // val result: Any = body.apply()

      val result: Any = body.apply()
      log.info("WorkExecutor got job {} [{}].", body.hashCode(), body.getClass)

      log.info("WorkExecutor has result: " + result + " with type " + result.getClass + " and hash " + result.hashCode())
      // sender ! WorkExecutor.WorkComplete( body.apply() )
      sender ! WorkExecutor.WorkComplete( result )
//    case n: Int ⇒
//      val n2 = n * n
//      val result = s"$n * $n = $n2"
//      sender ! WorkExecutor.WorkComplete(result)
  }
}