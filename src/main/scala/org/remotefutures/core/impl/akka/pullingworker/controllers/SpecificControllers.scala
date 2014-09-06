package org.remotefutures.core.impl.akka.pullingworker.controllers

import akka.actor.{ActorRef, ActorSystem}
import akka.cluster.Cluster
import akka.contrib.pattern.DistributedPubSubExtension
import org.remotefutures.core.NodeController
import org.remotefutures.core.impl.akka.pullingworker.PullingWorkerSettings
import org.remotefutures.core._

// specific node types for pulling worker
sealed trait PullingWorkerNodeType extends NodeType
case object FrontEndNodeType extends PullingWorkerNodeType
case object WorkerNodeType extends PullingWorkerNodeType
case object MasterNodeType extends PullingWorkerNodeType

case class FrontEndInformation( system: ActorSystem, mediator: ActorRef ) extends NodeInformation[FrontEndNodeType.type]
case object WorkerInformation extends NodeInformation[WorkerNodeType.type]
case object MasterInformation extends NodeInformation[MasterNodeType.type]


class PullingWorkerNodeControllers extends NodeControllers {

  val controllers: Map[NodeType, NodeController] = Map(
    ( FrontEndNodeType, new FrontendController(null) ),
    ( WorkerNodeType, new WorkerController(null) ),
    ( MasterNodeType, new MasterController(null) )
  )

  override def nodeController(nodeType: NodeType): NodeController = {
    controllers(nodeType)
  }

  // override def specificNodeController[T, C](nodeType: T)(implicit toConcrete: ToConcreteType[NodeController, C]): C = {
  override def specificNodeController[C](nodeType: NodeType)(implicit toConcrete: ToConcreteType[NodeController, C]): C = {
    toConcrete.convert( controllers(nodeType) )
  }

  override def nodeTypes: Set[NodeType] = Set(FrontEndNodeType, WorkerNodeType, MasterNodeType)
}

object Implicits {
  implicit val frontEndToConcrete = new ToConcreteType[NodeController, FrontendController]
  implicit val workerToConcrete = new ToConcreteType[NodeController, WorkerController]
  implicit val masterToConcrete = new ToConcreteType[NodeController, MasterController]
}



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
