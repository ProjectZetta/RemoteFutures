
package org.remotefutures.core

import scala.concurrent.Future

/**
  */
object RemoteFuture {

  def apply[T](body: => T)(implicit executor: RemoteExecutionContext): Future[T] = impl.RemoteFuture(body)

}
