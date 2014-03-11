/* Copyright (c) 2014 Marvin Hansen.
* www.marvin - hansen.tel.
* ALl RIGHTS RESERVED
***************************
* Project: DistributedRemoteFutures
* User: Marvin Hansen
  * Web: www.marvin - hansen.tel
* Date: 3 / 11 / 14(/ dd / mm / yy)
* Time: 6: 27 PM (CET)
*/
package main.core

import main.core.Distribution._
import java.net.InetAddress
import scala.concurrent.duration.Duration


/*  http://keramida.wordpress.com/2013/06/19/factory-objects-in-scala-code/  */
object RemoteConfig {
  /**
   * Remote configuration wraps all relevant parameters in one Singelton object.
   *
   *
   * @param host  INet host for execution.
   * @param dur    Max duration for timeout. For instance 5 seconds
   * @param poolSize size of the remote thread pool. This should actually disappear in the future and replaced with a useful default parameter
   * @param dist Distribution strategy. See @main.core.Distribution for details.
   * @return a configuration for a Remote Future
   */
  def apply(host: InetAddress, dur: Duration, poolSize: Int, dist: Distribution) = {
    new RemoteConfig {
      override def duration: Duration = dur

      override def distribution: Distribution = dist

      override def remoteHost: InetAddress = host

      override def threadPoolSize = poolSize
    }
  }
}


//* Interface */
trait RemoteConfig {
  def remoteHost: InetAddress

  def duration: Duration

  def distribution: Distribution

  def threadPoolSize: Int
}