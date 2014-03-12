/* Copyright (c) 2014 Marvin Hansen.
 * www.marvin-hansen.tel.
 * ALl RIGHTS RESERVED
 ***************************
 * Project: DistributedRemoteFutures
 * User: Marvin Hansen
 * Web: www.marvin-hansen.tel
 * Date: 3/11/14 (/dd/mm/yy)
 * Time: 6:43 PM (CET)
 */
package org.remotefutures.examples


import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit
import org.remotefutures.core.Remote
import org.remotefutures.util.LocalExecutor
import LocalExecutor.Implicits.LocalConfig
import LocalExecutor.Implicits.LocalExecution

object SimpleRemote extends App {

  final val T = Duration(2, TimeUnit.SECONDS)


  val rmt = Remote {
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
