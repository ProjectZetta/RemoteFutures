/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.core.impl.hazelcast

import com.hazelcast.core.Hazelcast

/**
 * Creates a new default (local) instance. Starting this class n-times
 * creates n-instances running locally but listing on different ports.
 *
 * Don't ask me why there is no other way...
 *
 * @author Marvin Hansen
 */
class HazelcastNode extends App {
  override def main(args: Array[String]): Unit = {
    Hazelcast.newHazelcastInstance
  }
}
