/*
 * Copyright (c) 2014 Martin Senne, Marvin Hansen.
 */
package org.remotefutures.examples

import org.remotefutures.core.{RemoteExecutor, DistributionStrategy, Config}
import java.net.InetAddress
import scala.concurrent.duration._
import scala.language.postfixOps


object TestRemoteExecutorCreation {
  def main(args: Array[String]): Unit = {
    val config = Config(InetAddress.getLocalHost, (2 seconds), 3, DistributionStrategy.FAIL_OVER, "org.remotefutures.core.DummyRemoteExecutor")
    val executor = RemoteExecutor(config)
    executor.execute(() => {}, Unit)

  }
}
