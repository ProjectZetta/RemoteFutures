/*
 * Copyright (c) 2014 Martin Senne
 */
package org.remotefutures.core.impl.akka.pullingworker.messages

/**
 *
 */
object MasterStatus {
  // from client to master
  case object IsMasterOperable

  // from master to frontend / client
  trait MasterOperable

  case object MasterIsOperable extends MasterOperable

  case object MasterIsNotOperable extends MasterOperable
}
