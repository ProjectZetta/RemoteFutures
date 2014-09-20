package org.remotefutures.runner

import akka.actor.Actor
import spray.json.DefaultJsonProtocol
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
// import spray.httpx.marshalling.Marshaller
import spray.httpx.encoding.{NoEncoding, Gzip}
// import spray.util._
import CachingDirectives._

import scala.xml.Elem

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

case class ProcessDesc(id: Int, description: String)
case class FullProcessDesc(id: Int, description: String, command:String, status: Int)

object ProcessJsonProtocol extends DefaultJsonProtocol {
  implicit val processDescFormat = jsonFormat2(ProcessDesc)
  implicit val fullProcessDescFormat = jsonFormat4(FullProcessDesc)
}

object Processes {
  var processes: List[Process] = List()
  
  
}


// / this trait defines our service behavior independently from the service actor
trait MyService extends HttpService {

  import spray.httpx.unmarshalling._
  import spray.httpx.marshalling._
  import ProcessJsonProtocol._
  import spray.httpx.SprayJsonSupport._
  import spray.util._


  // we use the enclosing ActorContext's or ActorSystem's dispatcher for our Futures and Scheduler
  implicit def executionContext = actorRefFactory.dispatcher

//  val myRoute2 =
//    path("") {
//      get {
//        respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
//          complete {
//            <html>
//              <body>
//                <h1>Say hello to <i>spray-routing</i> on <i>spray-can</i>!</h1>
//              </body>
//            </html>
//          }
//        }
//      }
//    }


//
//    path("testpost") {
//      decompressRequest(Gzip, NoEncoding) {
//        entity(as[String]) { content: String =>
//          complete(s"Request content: '$content'")
//        }
//      }
//    } ~

  val myRoute =
    initPage ~
    rest ~
    path("stats") {
      get {
        complete {
          actorRefFactory.actorFor("/user/IO-HTTP/listener-0")
            .ask(Http.GetStats)(1.second)
            .mapTo[Stats]
        }
      }
    }



  def rest: Route = {
    pathPrefix("api") {
      path("processes") {
        get { ctx =>
          val process1: ProcessDesc = new ProcessDesc( 8475, "A funny process")
          val process2: ProcessDesc = new ProcessDesc( 2634, "Another funny process")
          val processes = List( process1, process2 )
          // List[ProcessDesc]
          ctx.complete(processes) // uses the in-scope Marshaller to convert the
        } ~
        post {
          entity(as[ProcessDesc]) {pd =>
            complete(s"Got processdescription: ${pd.id} - ${pd.description}")
          }
        }
      } ~
      path("processes" / HexIntNumber) { id =>
        get {
          complete {
            "Received GET request for order " + id
          }
        } ~
          put {
            complete {
              "Received PUT request for order " + id
            }
          }
      }
    }
  }

  def initPage: Route = {
    path("") {
      get {
        respondWithMediaType(`text/html`) {
          // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
                <h1>Runner</h1>
                <form name="input" action="startjob" method="post">
                  Command:
                  <input type="text" name="command"/>
                  <input type="submit" value="Submit"/>
                </form>
              </body>
            </html>
          }
        }
      }
    } ~
    path("startjob") {
      decompressRequest(Gzip, NoEncoding) {
        entity(as[String]) { content: String =>
          complete(s"Request content: '$content'")
        }
      }
      // https://github.com/markkolich/spray-servlet-webapp/blob/master/src/main/scala/com/kolich/spray/templating/ScalateSupport.scala
      // complete { <html></html> }

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