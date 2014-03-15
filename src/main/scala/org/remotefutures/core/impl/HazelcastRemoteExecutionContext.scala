package org.remotefutures.core.impl


// import com.hazelcast.config.{Config => HazelConfig, ExecutorConfig => HazelExecutorConfig}



//    implicit val LocalConfig: Config = {
//      val host = InetAddress.getLocalHost
//      val poolSize = 4
//      //  Thread Pool size on Remote host
//      val dist = DistributionStrategy.LOAD_BALANCING
//      val T = Duration(2, TimeUnit.SECONDS)
//      val config: Config = Config(host, T, poolSize, dist, "Need an Hazelcast RemoteExecutor Instance here")
//      config
//    }
//
//    implicit val LocalExecution: IExecutorService = {
//      //http://www.hazelcast.org/docs/latest/manual/html-single/hazelcast-documentation.html#distributed-executor-service
//      val execConf = new HazelExecutorConfig().setName("RemoteExecutor").setPoolSize(LocalConfig.threadPoolSize)
//      val conf: HazelConfig = new HazelConfig().addExecutorConfig(execConf)
//      val h: HazelcastInstance = Hazelcast.newHazelcastInstance(conf)
//      //create & return remote executor
//      val executor = h.getExecutorService("my-distributed-executor")
//      executor
//    }