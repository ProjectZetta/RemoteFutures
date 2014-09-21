package org.remotefutures.runner_client

import org.remotefutures.runner.{NewProcess, ProcessDesc}
import spray.http.HttpResponse

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
  def start(command: String): Int

  /**
   * Stop the execution of the command.
   *
   * @param id
   */
  def stop(id: Int)

  /**
   *
   * @param id
   */
  def status(id: Int)
}

object Main extends App {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("runner-client")

  val log = Logging(system, getClass)

  log.info( "args: " + args.mkString(", ") )

  import system.dispatcher // execution context for futures below


  val hostname = "http://127.0.0.1:8080"
  val restUri = hostname + "/api"
  val processesUri = restUri + "/processes"

  if (args.length >= 1) {
    val command: String = args(0)
    println("Command is '" + command + "'")
    command match {
      case "create" => {
        val pipeline = sendReceive ~> unmarshal[ProcessDesc]
        // log.info("Creating " + args(1))
        log.info("Creating ")
        val responseFuture = pipeline {
          Post(processesUri, NewProcess("ls -la"))
        }
        responseFuture onComplete {
          case Success(x: ProcessDesc) => {
            log.info("Successful creation. Got new process description: " + x)
            shutdown
          }
          case Success(unexpected) => {
            log.info("Something unexpected")
            shutdown
          }
          case Failure(error) => {
            log.error("Error " + error)
            shutdown
          }
        }
      }
      case "list" => {
        val pipeline = sendReceive ~> unmarshal[Array[ProcessDesc]]

        log.info("GET on " + processesUri)

        val responseFuture = pipeline {
          Get(processesUri)
        }
        responseFuture onComplete {
          case Success(x: Array[ProcessDesc]) => {
            log.info("Got x ;) " + x)
            x.foreach(x => println(x))
            shutdown
          }
          case Success(unexpected) => {
            log.info("Something unexpected")
            shutdown
          }
          case Failure(error) => {
            log.error("Error " + error)
            shutdown
          }
        }
      }
      case _ => {
        log.error("Unknown command.")
      }
    }
  } else {
    log.error("Not enough arguments.")
  }


  def shutdown(): Unit = {
    IO(Http).ask(Http.CloseAll)(1.second).await
    system.shutdown()
  }
}