/*
 * Copyright (c) 2014 Martin Senne
 */
package org.remotefutures.core.impl.akka.pullingworker

import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import akka.actor._
import akka.cluster.Cluster
import akka.contrib.pattern.ClusterClient
import akka.contrib.pattern.ClusterSingletonManager
import akka.japi.Util.immutableSeq

//object Main extends Startup {
//
//  def main(args: Array[String]): Unit = {
//    val joinAddress = startMaster(None, "backend")
//    Thread.sleep(5000)
//    startMaster(Some(joinAddress), "backend")
//    // startWorker(joinAddress)
//    startWorker
//    // Thread.sleep(5000)
//    // startFrontend(joinAddress)
//  }
//
//}

trait Startup {

  def masterSystemName = "Mastersystem"
  def workerSystemName = "Workersystem"
  def workTimeout = 10.seconds


  /**
   *
   * @param joinAddressOption
   * @return join address of cluster
   */
  def startMaster(joinAddressOption: Option[Address]): Address = {
    val role: String = "backend"

    println("Starting master")

    val conf = ConfigFactory.parseString(s"akka.cluster.roles=[$role]").
      withFallback(ConfigFactory.load()) // using application.conf right now
    val system = ActorSystem(masterSystemName, conf)
    val joinAddress = joinAddressOption.getOrElse(Cluster(system).selfAddress)

    println("  This master node is joining the cluster at join address " + joinAddress)

    Cluster(system).join(joinAddress)

    // create the master actor (as cluster singleton).
    system.actorOf(ClusterSingletonManager.props(Master.props(workTimeout), "active", PoisonPill, Some(role)), "master")

    joinAddress
  }


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

    val workerConfigName = "worker"

    val conf = ConfigFactory.load( workerConfigName );

    val system = ActorSystem(workerSystemName, conf)

    val initialContacts: Set[ActorSelection] = immutableSeq(conf.getStringList("contact-points")).map {
      case AddressFromURIString(addr) ⇒ system.actorSelection(RootActorPath(addr) / "user" / "receptionist")
    }.toSet

    println("  Worker uses contact points " + initialContacts + " to contact master.")

    val clusterClient = system.actorOf(ClusterClient.props(initialContacts), "clusterClient")

    // create the worker actor
    system.actorOf(Worker.props(clusterClient, Props[WorkExecutor]), "worker")
  }
}
