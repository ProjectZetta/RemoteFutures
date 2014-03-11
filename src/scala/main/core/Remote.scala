/* Copyright (c) 2014 Marvin Hansen.
 * www.marvin-hansen.tel.
 * ALl RIGHTS RESERVED
 ***************************
 * Project: DistributedRemoteFutures
 * User: Marvin Hansen
 * Web: www.marvin-hansen.tel
 * Date: 3/11/14 (/dd/mm/yy)
 * Time: 6:38 PM (CET)
 */
package main.core

import scala.util.{Failure, Success}
import scala.util.control.NonFatal
import com.hazelcast.core.IExecutorService
import scala.concurrent.Future

/**
 * A remote provides a simple facility to execute arbitrary code
 * on a remote host by using a remote executor. The result of
 * a Remote is wrapped into a Future thus
 * can be freely composed and combined with other Futures.
 */
object Remote {

  /**
   * Wraps arbitrary code in a Future
   *
   * @param body code to execute
   * @tparam T return type of the code
   */
  private class PromiseCompletingRunnable[T](body: => T) extends Runnable with Serializable {
    final val promise = concurrent.Promise[T]()

    override def run() = {
      promise complete {
        try Success(body) catch {
          case NonFatal(e) => Failure(e)
        }
      }
    }
  }

  /**
   * "Modification is undesirable, but modifiability is paramount"
   * --Paul Phillips
   *
   *
   *
   *
   *
   * @param body Code to execute
   * @param config exact settings how to execute the code remotely
   * @tparam T type oif the code to execute
   * @return  Result of the remote computation.
   *
   */
  def apply[T](body: => T)(implicit config: RemoteConfig, executor: IExecutorService): Future[T] = {

    val runnable: PromiseCompletingRunnable[T] = new PromiseCompletingRunnable(body)

    config.distribution match {
      case Distribution.FIRST_WIN => executor.executeOnAllMembers(runnable)
      case Distribution.LOAD_BALANCING => executor.execute(runnable)
      case Distribution.FAIL_OVER => None // @TODO
    }
    runnable.promise.future
  }

  override def finalize(): Unit = {
    super.finalize()
  }
}

