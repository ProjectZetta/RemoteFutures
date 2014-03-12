/* Copyright (c) 2014 Marvin Hansen.
 * www.marvin-hansen.tel.
 * ALl RIGHTS RESERVED
 ***************************
 * Project: DistributedRemoteFutures
 * User: Marvin Hansen
 * Web: www.marvin-hansen.tel
 * Date: 3/11/14 (/dd/mm/yy)
 * Time: 6:26 PM (CET)
 */
package main.core

/**
 * ENUM for configuring remote execution.
 *
 * FIRST_WIN means, the task is send to all nodes in a cluster
 * but only the first reply will be accepted. This is ideal for
 * low latency applications.
 *
 * LOAD_BALANCING means, the work load is evenly spread across
 * all nodes available.
 *
 * FAIL_OVER means, a task is send to one node and if an error or
 * time-out happens, the same task will be re-send to another node
 * so that eventually a result will be result.
 */
object Distribution extends Enumeration {
  type Distribution = Value
  val FIRST_WIN = Value(1)
  val LOAD_BALANCING = Value(2)
  val FAIL_OVER = Value(3)
}
