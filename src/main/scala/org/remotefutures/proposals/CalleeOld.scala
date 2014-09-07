package org.remotefutures.proposals

/**
 * Created by martin on 03.08.14.
 */
class CalleeOld {

}


//    val result: Future[T] = (remoteProducerActor ? msg).asInstanceOf[Future[T]]
//
//    // complete the promise
//    result.onComplete {
//      case x ⇒ {
//        println("THIS is the IMPORTANT MESSAGE: Got result " + x + " in execute(...).")
//        promise.complete(x)
//      }
//    }


// old 3 =====================================================
//    val remoteProducerActor = frontendSetup.system.actorOf(
//      Props(classOf[RemoteProducerActor], frontendSetup.mediator, promise))
//
//    println("Sending execute to actor " + remoteProducerActor)
//    remoteProducerActor ! Execute( () ⇒ body )


// old 2 =====================================================
//    val work = Work(nextWorkId(), 1)
//    val r: Future[Any] = frontEnd.mediator ? Send("/user/master/active", work, localAffinity = false)
//
//    r onComplete {
//      case x ⇒ {
//        println("Job transmitted result: " + x + " with Class " + x.getClass)
//      }
//    }


// old 1 (very old )=====================================================
// val result: Future[Try[T]] = (service ? Execute( () ⇒ body )).asInstanceOf[Future[Try[T]]]


// Explanation:
// - The actor, asked by ?, sends back either Success or Failure
// - onComplete is invoked by Try(M), if the asked actor return message M
// - The asked actor returns a message Success or Failure
// -- Thus on successful reception of a message from the actor, onComplete is invoked
//    either with Success(Success(_)) or
//    if the actor did not execute successfully with Success(Failure(_))
// -- if the future (by ?) instead falls into a timeout, we have Failure(_)
//    result onComplete {
//      case Success(Success(x)) ⇒ {
//        println("Success result of ask is " + x)
//        promise.success(x)
//      }
//      case Success(Failure(t)) ⇒ {
//        // problem on worker site ..... not a problem on master site
//        println("Failure result of ask is " + t)
//        promise.failure(t)
//      }
//      case Failure(x) ⇒ {
//        println("Failure due to other problems ")
//        promise.failure(x)
//      }
//    }