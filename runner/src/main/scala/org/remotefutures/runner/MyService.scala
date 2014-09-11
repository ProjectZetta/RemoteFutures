package org.remotefutures.runner

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._
import akka.actor.{Props, Actor}
import akka.pattern.ask

import scala.concurrent.duration._
import akka.actor._
import akka.pattern.ask
import spray.routing.{HttpService, RequestContext}
import spray.routing.directives.CachingDirectives
import spray.can.server.Stats
import spray.can.Http
import spray.httpx.marshalling.Marshaller
import spray.httpx.encoding.Gzip
import spray.util._
import spray.http._
import MediaTypes._
import CachingDirectives._

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with MyService {
  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context
  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}
// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService {


  // we use the enclosing ActorContext's or ActorSystem's dispatcher for our Futures and Scheduler
  implicit def executionContext = actorRefFactory.dispatcher

  val myRoute2 =
    path("") {
      get {
        respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
                <h1>Say hello to <i>spray-routing</i> on <i>spray-can</i>!</h1>
              </body>
            </html>
          }
        }
      }
    }

  val myRoute =
    path("stats") {
      get {
        complete {
          actorRefFactory.actorFor("/user/IO-HTTP/listener-0")
            .ask(Http.GetStats)(1.second)
            .mapTo[Stats]
        }
      }
    }
    path("command") {
      post {
        respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
                <h1>Say hello to <i>spray-routing</i> on <i>spray-can</i>!</h1>
              </body>
            </html>
          }
        }
      }
    }

  implicit val statsMarshaller: Marshaller[Stats] =
  Marshaller.delegate[Stats, String](ContentTypes.`text/plain`) { stats =>
    "Uptime : " + stats.uptime.formatHMS + '\n' +
      "Total requests : " + stats.totalRequests + '\n' +
      "Open requests : " + stats.openRequests + '\n' +
      "Max open requests : " + stats.maxOpenRequests + '\n' +
      "Total connections : " + stats.totalConnections + '\n' +
      "Open connections : " + stats.openConnections + '\n' +
      "Max open connections : " + stats.maxOpenConnections + '\n' +
      "Requests timed out : " + stats.requestTimeouts + '\n'
  }

  val test = path("") { complete { "a" }  }

  val myTestRoute: Route = {
    ctx => ctx.complete("asdf")
  }
}