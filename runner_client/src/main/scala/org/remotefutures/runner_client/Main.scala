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




//val pipeline: Future[SendReceive] =
//for (
//  Http.HostConnectorInfo(connector, _) <-
//      IO(Http) ? Http.HostConnectorSetup("www.spray.io", port = 80)
//  ) yield sendReceive(connector)
//
//val request = Get("/")
//val response: Future[HttpResponse] = pipeline.flatMap(_(request))

object Main extends App {

  // val hostname = "http://127.0.0.1:8080"

  val system = ActorSystem("runner-client")
  val control = SimpleProcessControl("127.0.0.1", 8080, system)
  import system.dispatcher

  def shutdown(): Unit = {
    IO(Http)(system).ask(Http.CloseAll)(1.second).await
    system.shutdown()
  }

  def createProcess(commandToExecute: String): Future[ProcessDesc] = {
    val res = control.start(commandToExecute)

    res andThen {
      case Success(x: ProcessDesc) => {
        println("Started successfully " + x)
      }
      case Success(unexpected) => {
        println("Warning: Something unexpected has happened: " + unexpected)
      }
      case Failure(error) => {
        println("Error " + error)
      }
    } andThen {
      case _ => shutdown
    }
  }




  val argLength = args.length

  if (argLength >= 1) {
    val command: String = args(0)

    command match {
      // ===================
      // create
      // ===================
      case "create" => {
        if (argLength == 2) {

          val commandToExecute = args(1)
          createProcess( commandToExecute )

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

        res andThen {
          case Success(x: List[ProcessDesc]) => {
            x.foreach(x => println(x))
          }
          case Success(unexpected) => {
            println("Warning: Something unexpected has happened: " + unexpected)
          }
          case Failure(error) => {
            println("Error " + error)
          }
        } andThen {
          case _ =>  shutdown
        }
      }

      case _ => {
        println("Unknown command. Aborting.")
      }
    }
  } else {
    println("Not enough arguments. Aborting.")
  }

}