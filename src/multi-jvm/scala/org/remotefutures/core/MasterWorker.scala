package org.remotefutures.core

import akka.actor.Address
import org.remotefutures.core.NodeControllers
import org.remotefutures.examples.FibonacciComputations
import org.remotefutures.spores._

import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}



/**
 * Start the master worker example with "multi-jvm:run org.remotefutures.core.MasterWorker"
 *
 * The master worker example consists of three nodes:
 * - master
 * - worker
 * - frontend
 */

/**
 * Frontend Node
 */
object MasterWorkerMultiJvmNode1 {

  import scala.concurrent.duration._
  import scala.concurrent.ExecutionContext.Implicits.global

  def main(args: Array[String]) {
    val controllers = NodeControllers.fromDefaultConfig
    val optFrontendController  = controllers("frontend")
    optFrontendController match {
      case Some(frontendController) => {
        val initParams = frontendController.start(123)
        val optRec = frontendController.executionContext( initParams )
        optRec match {
          case Some(rec) => {

            println("Waiting for operable")
            rec.isOperable()
            println("Finished with waiting for master to become operable.")

            // Lets start a remote future calculation here
            // val from = 1000000000L
            val from = 1000L
            val size = 10L

            val xs3: List[Long] = (from to from + size).toList

            println("Remote: " + xs3)

            implicit val implicitRemoteExecutionContext = rec

            // uses new apply method in RemoteFuture
            //   def apply[T](spore: NullarySpore[T])(implicit res: RemoteExecutionContext): Future[T] = impl.RemoteFutureImpl[T](spore)
            val fs: List[Future[BigInt]] = xs3.map(x => RemoteFuture {
              spore {
                val r = x
                () => {
                  FibonacciComputations.fibBigInt(r)
                }
              }
            })

            val r: Future[List[BigInt]] = Future sequence (fs)

            r onComplete {
              case Success(x) => {
                println("Really cool. All remote futures were computed.")
                println(x)
              }
              case Failure(t) => {
                println("Problem " + t)

              }
            }

            // Await.result(r, 10.seconds)



          }
          case None => throw new Exception("No execution context available. Aborting")
        }

      }
      case None => throw new Exception("Can not start frontend node. Aborting")
    }

    // val joinAddress = startMaster(None, "backend")
    // println("Master has default address: " + InetAddress.getLocalHost.getHostAddress );
    // startMaster(Some(joinAddress), "backend")
  }
}

object MasterWorkerMultiJvmNode2 {
  def main(args: Array[String]) {
    val controllers = NodeControllers.fromDefaultConfig
    val masterController = controllers("master")
    masterController match {
      case Some(x) => x.start(6234) // port currently ignored
      case None => throw new Exception("Can not start master node. Aborting")
    }
  }
}

object MasterWorkerMultiJvmNode3 {
  def main(args: Array[String]) {
    val controllers = NodeControllers.fromDefaultConfig
    val workerController = controllers("worker")
    workerController match {
      case Some(x) => x.start(0) // port currently ignored
      case None => throw new Exception("Can not start master node. Aborting")
    }
  }
}

// implicit val rec = org.remotefutures.core.RemoteExecutionContextImplicits.defaultConfigBasedRemoteExecutionContext

/*
Why worker registered again?

[JVM-2] [INFO] [09/07/2014 14:58:32.682] [Mastersystem-akka.actor.default-dispatcher-4] [akka.tcp://Mastersystem@127.0.0.1:2551/user/master/active] Master got work.
[JVM-2] [INFO] [09/07/2014 14:58:32.682] [Mastersystem-akka.actor.default-dispatcher-4] [akka.tcp://Mastersystem@127.0.0.1:2551/user/master/active] Accepted work: Work(2f8af6e7-2fba-4146-886f-85526ad6312f,<function0>)
[JVM-2] [INFO] [09/07/2014 14:58:32.688] [Mastersystem-akka.actor.default-dispatcher-14] [akka.tcp://Mastersystem@127.0.0.1:2551/user/master/active] Giving worker 337fc87a-b39e-48be-a44c-735acc4c8025 some work <function0>
[JVM-3] [INFO] [09/07/2014 14:58:32.695] [Workersystem-akka.actor.default-dispatcher-2] [akka.tcp://Workersystem@127.0.0.1:52715/user/worker] Worker got work.
[JVM-2] [INFO] [09/07/2014 14:58:45.608] [Mastersystem-akka.actor.default-dispatcher-19] [akka.tcp://Mastersystem@127.0.0.1:2551/user/master/active] Work timed out: WorkAndClient(Actor[akka.tcp://Mastersystem@127.0.0.1:2553/user/$c#-215176572],Work(48979d4e-ff6e-4231-bb2e-23d206b6d083,<function0>))
[JVM-2] [INFO] [09/07/2014 14:58:49.513] [Mastersystem-akka.actor.default-dispatcher-17] [akka.tcp://Mastersystem@127.0.0.1:2551/user/master/active] Worker registered: 337fc87a-b39e-48be-a44c-735acc4c8025
*/


