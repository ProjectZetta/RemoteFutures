/*
 * Copyright (c) 2014 Martin Senne
 */
package org.remotefutures.core.impl.akka.pullingworker.controllers

import akka.actor._
import akka.cluster.Cluster
import akka.contrib.pattern.DistributedPubSubExtension
import akka.japi.Util._
import org.remotefutures.core.{RemoteExecutionContext, NodeInformation, NodeController}
import org.remotefutures.core.impl.akka.pullingworker.{PullingWorkerRemoteExecutionContext, PullingWorkerSettings}

case class FrontEndInformation( system: ActorSystem, mediator: ActorRef ) extends NodeInformation[FrontEndNodeType.type]

/**
 * Front end controller
 * @param settings describing frontend settings
 */
class FrontendController(settings: PullingWorkerSettings) extends NodeController {
  type S = FrontEndInformation
  type N = FrontEndNodeType.type

  override def start(port: Int): S = {
    println("Frontend controller: Starting")

    val systemname = settings.frontendSystemname
    val system = ActorSystem(systemname, settings.frontendAkkaSettings)
    val joinAddress = AddressFromURIString( settings.frontendJoinAddress )
    println("  Join Address is : " + joinAddress)
    Cluster(system).join(joinAddress)
    val mediator = DistributedPubSubExtension(system).mediator

    println("Frontend controller: Start finished. Mediator is " + mediator)

    FrontEndInformation(system, mediator)
  }

  override def stop: Unit = {}

  override def executionContext(init: S) : Option[RemoteExecutionContext] =
    Some(new PullingWorkerRemoteExecutionContext(init))

}
