package org.remotefutures.core.impl.akka.pullingworker

import scala.concurrent.duration._
import akka.actor.Actor
import akka.contrib.pattern.DistributedPubSubExtension
import akka.contrib.pattern.DistributedPubSubMediator.Send
import akka.pattern._
import akka.util.Timeout
import scala.concurrent.Future

object Frontend {
  case object Ok
  case object NotOk
}

class Frontend extends Actor {
  import Frontend._
  import context.dispatcher

  val mediator = DistributedPubSubExtension(context.system).mediator

  def receive = {
    case work =>
      implicit val timeout = Timeout(5.seconds)
      (mediator ? Send("/user/master/active", work, localAffinity = false)) map {
        case Master.Ack(_) => Ok
      } recover { case _ => NotOk } pipeTo sender

//      val f: Future[Any] = mediator ? Send("/user/master/active", work, localAffinity = false)
//      val f2: Future[Ok.type] = f map { case Master.Ack(_) => Ok }
//      val f3: Future[Product with Serializable] = f2 recover { case _ => NotOk }
//      val f4: Future[Product with Serializable] = f3 pipeTo sender

  }

}