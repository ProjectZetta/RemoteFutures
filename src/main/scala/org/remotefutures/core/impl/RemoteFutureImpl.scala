package org.remotefutures.core.impl

import org.remotefutures.core.RemoteExecutionContext
import scala.concurrent.{Promise, Future}

/**
 * A remote provides a simple facility to execute arbitrary code
 * on a remote host by using a remote executor. The result of
 * a Remote is wrapped into a Future thus
 * can be freely composed and combined with other Futures.
 */
object RemoteFutureImpl {

  /**
   * "Modification is undesirable, but modifiability is paramount"
   * --Paul Phillips
   *
   * @param body Code to execute
   * @param res: RemoteExecutionContext
   * @tparam T type oif the code to execute
   * @return  Result of the remote computation.
   *
   */
  def apply[T](body: => T)(implicit res: RemoteExecutionContext): Future[T] = {
    val p = Promise[T]
    res.execute(() => body, null, p)
    p.future
  }

  override def finalize(): Unit = {
    super.finalize()
  }
}