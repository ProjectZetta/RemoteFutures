/*
 * Copyright (c) 2014 Martin Senne
 */
package org.remotefutures.core.impl.akka.pullingworker.controllers

import akka.actor.{PoisonPill, ActorSystem, Address}
import akka.cluster.Cluster
import akka.contrib.pattern.ClusterSingletonManager
import com.typesafe.config.ConfigFactory
import org.remotefutures.core.{RemoteExecutionContext, NodeInformation, NodeController}
import org.remotefutures.core.impl.akka.pullingworker.{Master, PullingWorkerSettings}

case object MasterInformation extends NodeInformation[MasterNodeType.type]

class MasterController(settings: PullingWorkerSettings) extends NodeController {
  type S = MasterInformation.type
  type N = MasterNodeType.type

  import scala.concurrent.duration._


  override def start(port: Int): S = {
    def workTimeout = 10.seconds

    /**
     *
     * @param joinAddressOption
     * @return join address of cluster
     */
    def startMaster(joinAddressOption: Option[Address]): Address = {
      val role: String = "backend"

      val conf = ConfigFactory.parseString(s"akka.cluster.roles=[$role]").
        withFallback( settings.masterAkkaSettings ) // using application.conf right now
      val system = ActorSystem(settings.masterSystemname, conf)
      val joinAddress = joinAddressOption.getOrElse(Cluster(system).selfAddress)

      println("  This master node is joining the cluster at join address " + joinAddress)

      Cluster(system).join(joinAddress)

      // create the master actor (as cluster singleton).
      system.actorOf(ClusterSingletonManager.props(Master.props(workTimeout), "active", PoisonPill, Some(role)), "master")

      joinAddress
    }

    println("Master controller: Starting")
    startMaster(None)
    println("Master controller: Start finished.")
    MasterInformation

  }

  override def stop: Unit = ???

  override def executionContext(init: S): Option[RemoteExecutionContext] = None
}
// for debugging: netstat -lntup