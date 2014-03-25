/* Copyright (c) 2014 Marvin Hansen.
 * www.marvin-hansen.tel.
 * ALl RIGHTS RESERVED
 ***************************
 * Project: DistributedRemoteFutures
 * User: Marvin Hansen
 * Web: www.marvin-hansen.tel
 * Date: 3/25/14 (/dd/mm/yy)
 * Time: 2:07 PM (CET)
 */
package org.remotefutures.core

import scala.concurrent.Future

/**
  */
object RemoteFuture {

  def apply[T](body: => T)(implicit executor: RemoteExecutionContext): Future[T] = impl.RemoteFuture(body)

}
