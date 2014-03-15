/*
 * Copyright (c) 2014 Martin Senne, Marvin Hansen.
 */
package org.remotefutures.core

import scala.concurrent.duration.{Duration, FiniteDuration}
import com.typesafe.config.{ConfigFactory, Config, ConfigObject}
import akka.util.Helpers.Requiring
import org.remotefutures.util.Helpers.ConfigOps


class Settings(val config: Config) {
  private val cc = config.getConfig("general")

  val FutureTimeout: Duration = {
    val key = "future-timeout-in-ms"
    cc.getString(key).toLowerCase match {
      case "off" ⇒ Duration.Undefined
      case _ ⇒ cc.getMillisDuration(key) requiring (_ > Duration.Zero, key + " > 0 ms, or off")
    }
  }

  val RemoteExecutionContextClassname : String = {
    cc.getString("remote-execution-context-classname")
  }
}

object Settings {
  def apply(c:Config) : Settings = {
    new Settings(c)
  }
}

// ==============================================================================
// Unsure, HOW to do it reasonable. Inheritence is a very limited solution ....
// ==============================================================================

//trait AkkaOnlySettings
//
//trait HazelcastOnlySettings {
//  val x = 5
//}
//
//class HazelcastSettings(config: Config) extends Settings(config) with HazelcastOnlySettings {
//
//}
//
//
//object Fun {
//  def main(args:Array[String]) : Unit = {
//    val c = ConfigFactory.load("blub")
//    val r:HazelcastSettings = new HazelcastSettings(c)
//
//  }
//}
//
//
///**
// * Configuration wraps all relevant parameters in one Singelton object.
// */
//object Config {
//  /**
//   * Create configuration.
//   *
//   * @param _remoteExecutorClassname specifies the fully qualified classname to create instances of.
//   * @param host  INet host for execution.
//   * @param dur    Max duration for timeout. For instance 5 seconds
//   * @param poolSize size of the remote thread pool. This should actually disappear in the future and replaced with a useful default parameter
//   * @param dist Distribution strategy. See @org.remotefutures.core.Distribution for details.
//   * @return a configuration for a Remote Future
//   */
//  def apply(host: InetAddress, dur: Duration, poolSize: Int, dist: DistributionStrategy, _remoteExecutorClassname: String) = {
//    new Config {
//
//      override def remoteExecutorClassname: String = _remoteExecutorClassname
//
//      override def duration: Duration = dur
//
//      override def distribution: DistributionStrategy = dist
//
//      override def remoteHost: InetAddress = host
//
//      override def threadPoolSize = poolSize
//    }
//  }
//}
//
//
///**
// * Reflects all
// */
//trait Config {
//  def remoteExecutorClassname: String
//
//  def remoteHost: InetAddress
//
//  def duration: Duration
//
//  def distribution: DistributionStrategy
//
//  def threadPoolSize: Int
//}