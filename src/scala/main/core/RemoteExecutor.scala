/* Copyright (c) 2014 Marvin Hansen.
 * www.marvin-hansen.tel.
 * ALl RIGHTS RESERVED
 ***************************
 * Project: DistributedRemoteFutures
 * User: Marvin Hansen
 * Web: www.marvin-hansen.tel
 * Date: 3/11/14 (/dd/mm/yy)
 * Time: 6:32 PM (CET)
 */
package main.core

import com.hazelcast.config.{Config, ExecutorConfig}
import com.hazelcast.core.{IExecutorService, HazelcastInstance, Hazelcast}
import java.net.InetAddress
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

/**
 * WORK IN PROGRESS
 *
 * Companion object that defines an implicit import for a local hazelcast
 * execution service.
 *
 * Will be replaced.
 *
 */
object RemoteExecutor extends RemoteExecutor {

  def LocalExecutor = Implicits.LocalExecutor

  object Implicits {
    implicit val LocalConfig: RemoteConfig = {
      val host = InetAddress.getLocalHost
      val poolSize = 4
      //  Thread Pool size on Remote host
      val dist = Distribution.LOAD_BALANCING
      val T = Duration(2, TimeUnit.SECONDS)
      val config: RemoteConfig = RemoteConfig(host, T, poolSize, dist)
      config
    }
    implicit val LocalExecutor = getRemoteExecutor(LocalConfig)
  }

}

/** Mixing interface and implementation together */
class RemoteExecutor extends RemoteExecutorInf with RemoteExecutorImpl

trait RemoteExecutorInf {

  def getRemoteExecutor(cfg: RemoteConfig): IExecutorService
}

trait RemoteExecutorImpl extends RemoteExecutorInf {
  override def getRemoteExecutor(cfg: RemoteConfig) = {
    //http://www.hazelcast.org/docs/latest/manual/html-single/hazelcast-documentation.html#distributed-executor-service
    val execConf = new ExecutorConfig().setName("RemoteExecutor").setPoolSize(cfg.threadPoolSize)
    val conf: Config = new Config().addExecutorConfig(execConf)
    val h: HazelcastInstance = Hazelcast.newHazelcastInstance(conf)
    //create & return remote executor
    h.getExecutorService("my-distributed-executor")
  }

}