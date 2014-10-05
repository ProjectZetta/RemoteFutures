package org.remotefutures.runner_client

import org.remotefutures.runner.{NewProcess, FullProcessDesc, ProcessDesc}
import org.remotefutures.runner.ProcessJsonProtocol

import scala.concurrent.Future
import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.event.Logging
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout

import spray.http.{HttpRequest, HttpResponse}
import spray.can.Http
import spray.client.pipelining._
import spray.httpx.SprayJsonSupport._
import spray.httpx.unmarshalling._
import spray.httpx.marshalling._
import spray.util._
import spray.json.{JsValue, RootJsonFormat, JsonFormat, DefaultJsonProtocol}
import spray.json.DefaultJsonProtocol._ // THIS IMPORT IS   E-S-S-E-N-T-I-A-L, as it defines marshalling for T <-> JSON


/**
 * Control processes
 */
trait ProcessControl {

  /**
   * Execute a given command.
   *
   * @param command
   * @return
   */
  def start(command: String): Future[ProcessDesc]

  /**
   * Stop the execution of the command.
   *
   * @param id
   */
  def stop(id: Int) : Future[ProcessDesc]

  /**
   *
   * @param id
   */
  def status(id: Int) : Future[FullProcessDesc]

  /**
   *
   * @return
   */
  def list() : Future[List[ProcessDesc]]
}



/**
 * Companion object to create a new process control for given host
 */
object SimpleProcessControl {
  def apply(hostname: String, port:Int, system: ActorSystem) : SimpleProcessControl = {
    new SimpleProcessControl(hostname, port, system)
  }
}

//// we need an ActorSystem to host our application in
//implicit val system = ActorSystem("runner-client")
//val log = Logging(system, getClass)
//import system.dispatcher

class SimpleProcessControl(hostname: String, port: Int, system: ActorSystem)
  extends ProcessControl {

  implicit val s = system
  // implicit execution context
  import system.dispatcher

  import org.remotefutures.runner.ProcessJsonProtocol.processDescFormat
  import org.remotefutures.runner.ProcessJsonProtocol.newProcessDescFormat
  import org.remotefutures.runner.ProcessJsonProtocol.fullProcessDescFormat

  implicit val timeout = Timeout(60.seconds)

  val pipeline: Future[SendReceive] = {
    for {
      Http.HostConnectorInfo(connector, _) <- IO(Http) ? Http.HostConnectorSetup( hostname, port )
    } yield sendReceive(connector)
  }

//  val request = Get("/")
//  val r: Future[HttpResponse] = pipeline.flatMap((sr: SendReceive) => sr(request))
//
//  val x = for {
//    sr <- pipeline
//  } yield sr ~> unmarshal[ProcessDesc]

  val log = Logging(system, getClass)

  // execution context for futures below

  val restUri = "/api"
  val processesUri = restUri + "/processes"

  /**
   * Execute a given command.
   *
   * @param command
   * @return id
   */
  override def start(command: String): Future[ProcessDesc] = {
//    val pipeline2 = sendReceive ~> unmarshal[ProcessDesc]
//
//    log.info("Creating new process.")
//
//    val responseFuture = pipeline2 {
//      Post(processesUri, NewProcess(command))
//    }
//    responseFuture
   val request = Post(processesUri, NewProcess(command) )

//    for {
//      sr <- pipeline
//      t = sr ~> unmarshal[ProcessDesc]
//    } yield t(request)

    val p2 = pipeline.map( sr => { sr ~> unmarshal[ProcessDesc] } )
    p2.flatMap( sr => sr(request) )
  }

  /**
   * Stop the execution of the command.
   *
   * @param id
   */
  override def stop(id: Int): Future[ProcessDesc] = ???

  /**
   *
   * @return
   */
  override def list(): Future[List[ProcessDesc]] = {
    val pipeline = sendReceive ~> unmarshal[List[ProcessDesc]]

    log.info("Retrieving list of processes. (GET on (" + processesUri + "))")

    val responseFuture = pipeline {
      Get(processesUri)
    }

    responseFuture
  }

  /**
   *
   * @param id
   */
  override def status(id: Int): Future[FullProcessDesc] = ???

}
