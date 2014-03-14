/*
 * Copyright (c) 2014 Martin Senne, Marvin Hansen.
 */
package org.remotefutures.examples

import org.remotefutures.core.DistributionStrategy
import java.net.InetAddress
import scala.concurrent.duration._
import scala.language.postfixOps
import com.typesafe.config.ConfigFactory


object TestRemoteExecutorCreation {
  def main(args: Array[String]): Unit = {
//    val config = Config(InetAddress.getLocalHost, (2 seconds), 3, DistributionStrategy.FAIL_OVER, "org.remotefutures.core.impl.executor.DummyRemoteExecutor")
//    val executor = RemoteExecutor(config)
//    executor.execute(() => {}, Unit)

  }
}

object TestConfigRead {
  def main(args: Array[String]): Unit = {

    // "config1" is just an example of using a file other than application.conf
    val config1 = ConfigFactory.load("complex1")

    // use the config ourselves
    println("config1, complex-app.something=" + config1.getString("complex-app.something"))

  }
}
