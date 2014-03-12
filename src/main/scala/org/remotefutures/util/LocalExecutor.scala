/* Copyright (c) 2014 Marvin Hansen.
 * www.marvin-hansen.tel.
 * ALl RIGHTS RESERVED
 ***************************
 * Project: DistributedRemoteFutures
 * User: Marvin Hansen
 * Web: www.marvin-hansen.tel
 * Date: 3/12/14 (/dd/mm/yy)
 * Time: 1:02 PM (CET)
 */
package org.remotefutures.util

import java.net.InetAddress
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit
import org.remotefutures.core.{DistributionStrategy, RemoteConfig}
import com.hazelcast.core.{Hazelcast, HazelcastInstance, IExecutorService}
import com.hazelcast.config.{Config, ExecutorConfig}

/**
  */
object LocalExecutor {

  object Implicits {
    implicit val LocalConfig: RemoteConfig = {
      val host = InetAddress.getLocalHost
      val poolSize = 4
      //  Thread Pool size on Remote host
      val dist = DistributionStrategy.LOAD_BALANCING
      val T = Duration(2, TimeUnit.SECONDS)
      val config: RemoteConfig = RemoteConfig(host, T, poolSize, dist)
      config
    }

    implicit val LocalExecution: IExecutorService = {
      //http://www.hazelcast.org/docs/latest/manual/html-single/hazelcast-documentation.html#distributed-executor-service
      val execConf = new ExecutorConfig().setName("RemoteExecutor").setPoolSize(LocalConfig.threadPoolSize)
      val conf: Config = new Config().addExecutorConfig(execConf)
      val h: HazelcastInstance = Hazelcast.newHazelcastInstance(conf)
      //create & return remote executor
      val executor = h.getExecutorService("my-distributed-executor")
      executor
    }

  }

}
