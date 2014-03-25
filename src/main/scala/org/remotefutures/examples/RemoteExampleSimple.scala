/*
 * Copyright (c) 2014 Martin Senne, Marvin Hansen.
 */
package org.remotefutures.examples


import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit
import org.remotefutures.core.RemoteFuture

//

import org.remotefutures.core.EnvironmentImplicits.DefaultConfigBasedRemoteExecutionContext


object RemoteExampleSimple extends App {

  final val T = Duration(2, TimeUnit.SECONDS)


  val rmt = RemoteFuture {
    println(Thread.currentThread.getName)
    42 * 23
  }

  val fut = Future {
    println(Thread.currentThread.getName)
    24 * 99 * 399
  }

  val r1: Int = Await.result(rmt, T)
  println("Done, remote result is: " + r1)

  val r2 = Await.result(fut, T)
  println("Done, future result is: " + r2)


  println("Combining remotes and futures ")
  val comb = for {
    r <- rmt
    f <- fut
  } yield r + f

  println("final result of remote AND future")
  comb onComplete {
    case Success(all) => println(all)
    case Failure(t) => println("An error happened: " + t.getMessage)
  }

}
