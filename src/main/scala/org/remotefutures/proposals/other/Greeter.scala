/*
 * Copyright (c) 2014 Martin Senne, Marvin Hansen.
 */
package org.remotefutures.proposals.other

import akka.actor._
import scala.concurrent.duration._

case object Greet

case class Greeting(message: String)

case class WhoToGreet(who: String)

class GreetPrinter extends Actor {
  override def receive: PartialFunction[Any, Unit] = {
    case Greeting(message) => println(message)
  }
}


class Greeter extends Actor {
  var greeting = ""

  // The new String interpolation is neat
  override def receive: PartialFunction[Any, Unit] = {
    case WhoToGreet(who) => greeting = s"hello, $who"
    case Greet => sender ! Greeting(greeting) // Send the current greeting back to the sender
  }
}

object HelloAkkaScala extends App {

  // Create the 'helloakka' actor system
  val system = ActorSystem("helloakka")

  // val cluster = Cluster(system)


  // Create the 'greeter' actor
  val greeter = system.actorOf(Props[Greeter], "greeter")

  // Create an "actor-in-a-box"
  val inbox = Inbox.create(system)

  // Tell the 'greeter' to change its 'greeting' message
  greeter.tell(WhoToGreet("akka"), ActorRef.noSender)

  // Ask the 'greeter for the latest 'greeting'
  // Reply should go to the "actor-in-a-box"
  inbox.send(greeter, Greet)

  // Wait 5 seconds for the reply with the 'greeting' message
  val Greeting(message1) = inbox.receive(5.seconds)
  println(s"Greeting: $message1")

  // Change the greeting and ask for it again
  greeter.tell(WhoToGreet("typesafe"), ActorRef.noSender)
  inbox.send(greeter, Greet)
  val Greeting(message2) = inbox.receive(5.seconds)
  println(s"Greeting: $message2")

  val greetPrinter = system.actorOf(Props[GreetPrinter])
  // after zero seconds, send a Greet message every second to the greeter with a sender of the greetPrinter
  system.scheduler.schedule(0.seconds, 1.second, greeter, Greet)(system.dispatcher, greetPrinter)

}
