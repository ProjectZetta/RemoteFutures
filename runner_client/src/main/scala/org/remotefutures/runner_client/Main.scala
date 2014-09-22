package org.remotefutures.runner_client

import org.remotefutures.runner.{FullProcessDesc, NewProcess, ProcessDesc}
import spray.http.HttpResponse

import scala.concurrent.{Future, Await}
import scala.util.{Success, Failure}
import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.event.Logging
import akka.io.IO
import spray.json.{JsValue, RootJsonFormat, JsonFormat, DefaultJsonProtocol}
import spray.can.Http
import spray.httpx.SprayJsonSupport
import spray.client.pipelining._
import spray.util._
import spray.httpx.marshalling.Marshaller
import spray.httpx.unmarshalling._
import spray.httpx.marshalling._
import spray.httpx.SprayJsonSupport._
import spray.util._

import spray.json._
import DefaultJsonProtocol._ // THIS IMPORT IS   E-S-S-E-N-T-I-A-L, as it defines marshalling for T <-> JSON

import org.remotefutures.runner.ProcessJsonProtocol.processDescFormat
import org.remotefutures.runner.ProcessJsonProtocol.newProcessDescFormat
import org.remotefutures.runner.ProcessJsonProtocol.fullProcessDescFormat


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

object SimpleProcessControl {
  def apply(hostname: String) : SimpleProcessControl = {
    new SimpleProcessControl(hostname)
  }
}

class SimpleProcessControl(hostname: String)
extends ProcessControl {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("runner-client")

  val log = Logging(system, getClass)

  import system.dispatcher

  // execution context for futures below

  val restUri = hostname + "/api"
  val processesUri = restUri + "/processes"

  /**
   * Execute a given command.
   *
   * @param command
   * @return id
   */
  override def start(command: String): Future[ProcessDesc] = {
    val pipeline = sendReceive ~> unmarshal[ProcessDesc]
    // log.info("Creating " + args(1))
    log.info("Creating new process.")
    val responseFuture = pipeline {
      Post(processesUri, NewProcess(command))
    }
    responseFuture
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



  def shutdown(): Unit = {
    IO(Http).ask(Http.CloseAll)(1.second).await
    system.shutdown()
  }
}

object Main extends App {

  val hostname = "http://127.0.0.1:8080"

  val control = SimpleProcessControl(hostname)

  import control.system.dispatcher


  val argLength = args.length

  if (argLength >= 1) {
    val command: String = args(0)

    command match {
      // ===================
      // create
      // ===================
      case "create" => {
        if (argLength == 2) {

          val res = control.start( args(1) )

          res onComplete {
            case Success(x: ProcessDesc) => {
              println("Started successfully " + x)
            }
            case Success(unexpected) => {
              println("Warning: Something unexpected has happened: " + unexpected)
            }
            case Failure(error) => {
              println("Error " + error)
            }
          }
          res onComplete { x => control.shutdown }

          // val res = Await.result(responseFuture, 10 seconds)

        } else {
          println("Invalid usage of create.")
        }
      }
      // ===================
      // list
      // ===================
      case "list" => {

        val res = control.list()

        res onComplete {
          case Success(x: List[ProcessDesc]) => {
            x.foreach(x => println(x))
          }
          case Success(unexpected) => {
            println("Warning: Something unexpected has happened: " + unexpected)
          }
          case Failure(error) => {
            println("Error " + error)
          }
        }
        res onComplete { x => control.shutdown }
      }

      case _ => {
        println("Unknown command. Aborting.")
      }
    }
  } else {
    println("Not enough arguments. Aborting.")
  }

}