package org.remotefutures.runner_client.old

import akka.actor.ActorSystem
import akka.event.Logging

import scala.util.{Failure, Success}

object Main2 extends App
with HostLevelApiDemo {

  val actorSystemName = "runnerClient"

  // with RequestLevelApiDemo {
  // we always need an ActorSystem to host our application in
  implicit val system = ActorSystem( actorSystemName )

  import org.remotefutures.runner_client.old.Main2.system.dispatcher

  // execution context for future transformations below
  val log = Logging(system, getClass)

  // the spray-can client-side API has three levels (from lowest to highest):
  // 1. the connection-level API
  // 2. the host-level API
  // 3. the request-level API
  //
  // this example demonstrates all three APIs by retrieving the server-version
  // of http://spray.io in three different ways
  val host = "spray.io"
  val result = for {
    result <- demoHostLevelApi(host)
  } yield Set(result)
  result onComplete {
    case Success(res) => log.info("{} is running {}", host, res mkString ", ")
    case Failure(error) => log.warning("Error: {}", error)
  }
  result onComplete { _ => system.shutdown()}
}