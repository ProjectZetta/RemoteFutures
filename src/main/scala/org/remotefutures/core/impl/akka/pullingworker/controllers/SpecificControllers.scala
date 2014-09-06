package org.remotefutures.core.impl.akka.pullingworker.controllers

import akka.actor.{ActorRef, ActorSystem}
import akka.cluster.Cluster
import akka.contrib.pattern.DistributedPubSubExtension
import org.remotefutures.core.NodeController
import org.remotefutures.core.impl.akka.pullingworker.PullingWorkerSettings
import org.remotefutures.core._

// specific node types for pulling worker scenario
sealed trait PullingWorkerNodeType extends NodeType
case object FrontEndNodeType extends PullingWorkerNodeType
case object WorkerNodeType extends PullingWorkerNodeType
case object MasterNodeType extends PullingWorkerNodeType

case class FrontEndInformation( system: ActorSystem, mediator: ActorRef ) extends NodeInformation[FrontEndNodeType.type]
case object WorkerInformation extends NodeInformation[WorkerNodeType.type]
case object MasterInformation extends NodeInformation[MasterNodeType.type]


class PullingWorkerNodeControllers extends NodeControllers {

  val frontEndController = new FrontendController(null)
  val masterController = new MasterController(null)
  val workerController = new WorkerController(null)

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


/**
 * Front end controller
 * @param settings describing frontend settings
 */
class FrontendController(settings: PullingWorkerSettings) extends NodeController {
  type S = FrontEndInformation
  type N = FrontEndNodeType.type

  override def start(port: Int): S = {
  // override def start(port: Int): NodeInformation[FrontEnd.type] = {
    println("Frontend: Starting")

    val systemname = settings.masterSystemname
    val system = ActorSystem(systemname)
    // Cluster(system).join(joinAddress)
    val mediator = DistributedPubSubExtension(system).mediator

    println("Frontend: Start finished. Mediator is " + mediator)

    FrontEndInformation(system, mediator)
  }

  override def stop: Unit = ???
}

class WorkerController(settings: PullingWorkerSettings) extends NodeController {
  type S = WorkerInformation.type
  type N = WorkerNodeType.type

  override def start(port: Int): S = ???

  override def stop: Unit = ???
}


class MasterController(settings: PullingWorkerSettings) extends NodeController {
  type S = MasterInformation.type
  type N = MasterNodeType.type

  override def start(port: Int): S = ???

  override def stop: Unit = ???
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
