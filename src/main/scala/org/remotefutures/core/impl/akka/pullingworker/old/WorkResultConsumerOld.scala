package org.remotefutures.core.impl.akka.pullingworker.old

import akka.actor.{Actor, ActorLogging}
import akka.contrib.pattern.{DistributedPubSubExtension, DistributedPubSubMediator}
import org.remotefutures.core.impl.akka.pullingworker.{Master, WorkResult}

class WorkResultConsumerOld extends Actor with ActorLogging {

  val mediator = DistributedPubSubExtension(context.system).mediator
  mediator ! DistributedPubSubMediator.Subscribe(Master.ResultsTopic, self)

  def receive = {
    case _: DistributedPubSubMediator.SubscribeAck =>
    case WorkResult(workId, result) =>
      log.info("Consumed result: {}", result)
  }

}