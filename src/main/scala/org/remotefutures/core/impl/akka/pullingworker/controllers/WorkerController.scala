/*
 * Copyright (c) 2014 Martin Senne
 */
package org.remotefutures.core.impl.akka.pullingworker.controllers

import akka.actor._
import akka.contrib.pattern.ClusterClient
import akka.japi.Util._
import com.typesafe.config.ConfigFactory
import org.remotefutures.core.{RemoteExecutionContext, NodeInformation, NodeController}
import org.remotefutures.core.impl.akka.pullingworker.{WorkExecutor, Worker, PullingWorkerSettings}

case object WorkerInformation extends NodeInformation[WorkerNodeType.type]

class WorkerController(settings: PullingWorkerSettings) extends NodeController {
  type S = WorkerInformation.type
  type N = WorkerNodeType.type

  override def start(port: Int): S = {

    /**
     * Setup worker node. This node is not member of the cluster.
     * Create a special actor "ClusterClient" on this node.
     * This cluster client communicates with a receptionist. The receptionist lives in the cluster.
     *
     * @see http://doc.akka.io/docs/akka/2.3.3/contrib/cluster-client.html
     *
     */
    def startWorker(): Unit = {
      println("Starting worker")

      val workerAkkaConfig = settings.workerAkkaSettings

      val system = ActorSystem( settings.workerSystemname, workerAkkaConfig )

      // conf.getStringList("contact-points")
      val initialContacts: Set[ActorSelection] = immutableSeq( settings.workerInitialContactPoint).map {
        case AddressFromURIString(addr) ⇒ {
          system.actorSelection(RootActorPath(addr) / "user" / "receptionist")
        }
      }.toSet

      println("  Worker uses contact points " + initialContacts + " to contact master.")

      val clusterClient = system.actorOf(ClusterClient.props(initialContacts), "clusterClient")

      // create the worker actor
      system.actorOf(Worker.props(clusterClient, Props[WorkExecutor]), "worker")
    }

    println("Worker controller: Starting")
    startWorker
    println("Worker controller: Start finished.")
    WorkerInformation
  }

  override def stop: Unit = ???

  override def executionContext(init: S): Option[RemoteExecutionContext] = None
}