/* Copyright (c) 2014 Marvin Hansen.
 * www.marvin-hansen.tel.
 * ALl RIGHTS RESERVED
 ***************************
 * Project: DistributedRemoteFutures
 * User: Marvin Hansen
 * Web: www.marvin-hansen.tel
 * Date: 3/12/14 (/dd/mm/yy)
 * Time: 12:44 PM (CET)
 */
package core

import scala.util.{Failure, Success}
import scala.util.control.NonFatal

/**
  */


/**
 * Wraps arbitrary code in a Future
 *
 * @param body code to execute
 * @tparam T return type of the code
 */
protected class PromiseCompletingRunnable[T](body: => T) extends Runnable with Serializable {
  final val promise = concurrent.Promise[T]()

  override def run() = {
    promise complete {
      try Success(body) catch {
        case NonFatal(e) => Failure(e)
      }
    }
  }
}
