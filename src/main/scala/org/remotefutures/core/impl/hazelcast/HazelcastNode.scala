/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.core.impl.hazelcast

import com.hazelcast.core.Hazelcast

/**
 * Creates a new default (local) instance. Starting this class n-times
 * creates n-instances running locally but listing on different ports.
 * Section 6.2 in "The book of Hazlecast"
 *
 * @author Marvin Hansen
 */
class HazelcastNode extends App {
  override def main(args: Array[String]): Unit = {
    Hazelcast.newHazelcastInstance
  }
}
