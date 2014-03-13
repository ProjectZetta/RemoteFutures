package org.remotefutures.core

import org.remotefutures.core.DistributionStrategy._
import java.net.InetAddress
import scala.concurrent.duration.Duration

/**
 * Configuration wraps all relevant parameters in one Singelton object.
 */
object Config {
  /**
   * Create configuration.
   *
   * @param _remoteExecutorClassname specifies the fully qualified classname to create instances of.
   * @param host  INet host for execution.
   * @param dur    Max duration for timeout. For instance 5 seconds
   * @param poolSize size of the remote thread pool. This should actually disappear in the future and replaced with a useful default parameter
   * @param dist Distribution strategy. See @org.remotefutures.core.Distribution for details.
   * @return a configuration for a Remote Future
   */
  def apply(host: InetAddress, dur: Duration, poolSize: Int, dist: DistributionStrategy, _remoteExecutorClassname : String) = {
    new Config {

      override def remoteExecutorClassname: String = _remoteExecutorClassname

      override def duration: Duration = dur

      override def distribution: DistributionStrategy = dist

      override def remoteHost: InetAddress = host

      override def threadPoolSize = poolSize
    }
  }
}


/**
 * Reflects all
 */
trait Config {
  def remoteExecutorClassname: String

  def remoteHost: InetAddress

  def duration: Duration

  def distribution: DistributionStrategy

  def threadPoolSize: Int
}