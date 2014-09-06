package org.remotefutures.core.impl.akka.pullingworker.controllers

import org.remotefutures.core.NodeController
import org.remotefutures.core.impl.akka.pullingworker.PullingWorkerSettings
import org.remotefutures.core._

// specific node types for pulling worker scenario

sealed trait PullingWorkerNodeType extends NodeType
case object FrontEndNodeType extends PullingWorkerNodeType
case object WorkerNodeType extends PullingWorkerNodeType
case object MasterNodeType extends PullingWorkerNodeType

/**
 * Holds controllers for all three node types (frontend, master, worker) in the pulling working scenario.
 * @param settings to specify the setup of the nodes of different type.
 */
class PullingWorkerNodeControllers(settings: PullingWorkerSettings) extends NodeControllers {

  val frontEndController = new FrontendController(settings)
  val masterController = new MasterController(settings)
  val workerController = new WorkerController(settings)

  val controllers: Map[NodeType, NodeController] = Map(
    ( FrontEndNodeType, frontEndController ),
    ( WorkerNodeType, workerController ),
    ( MasterNodeType, masterController )
  )

  val mapping: Map[String, NodeType] = Map(
    ( "worker", WorkerNodeType ),
    ( "master", MasterNodeType ),
    ( "frontend", FrontEndNodeType )
  )

  override def apply(nodeType: NodeType): NodeController = {
    controllers(nodeType)
  }

  override def apply(nodeTypeDesc: String): Option[NodeController] = {
    mapping.get(nodeTypeDesc).map( x => apply(x) )
  }
}





//
///**
// *
// * @param joinAddress
// * @param systemName
// */
//class FrontendSetup(joinAddress: akka.actor.Address, systemName: String) {
//  val system = ActorSystem(systemName)
//  Cluster(system).join(joinAddress)
//  val mediator = DistributedPubSubExtension(system).mediator
//  println("Frontend Setup finished. Mediator is " + mediator)
//
//  //   val remoteProducerActor = system.actorOf( Props(classOf[RemoteProducerActor], mediator))
//
//  //  val frontend = actorSystem.actorOf(Props[Frontend], "frontend")
//  //  actorSystem.actorOf(Props(classOf[WorkProducer], frontend), "producer")
//  //  actorSystem.actorOf(Props[WorkConsumer], "consumer")
//}




//
//  // =====================================================
//  // this is the code to setup other nodes
//  // =====================================================
////  println("Starting up pulling worker (final) cluster.")
//  val joinAddress = startMaster(None, "backend")
////  Thread.sleep(5000)
////  // startBackend(Some(joinAddress), "backend")
////  // startWorker(joinAddress)
////  startWorker
//  // =====================================================

//  val joinAddress = null
//  val masterSystemName = ""
//