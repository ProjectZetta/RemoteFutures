
package org.remotefutures.core.impl.hazelcast

import org.remotefutures.util.Debug._
import scala.concurrent.Promise
import com.hazelcast.core.Hazelcast
import org.remotefutures.core.impl.{PromiseCompletingRunnable, RemoteExecutor}


class HazelcastRemoteExecutor extends RemoteExecutor {

  // switches debugging on and off
  implicit final val DBG = true

  final val hz = Hazelcast.newHazelcastInstance()
  final val executor = hz.getExecutorService("default-executor")


  /**
   * Execute function {@code fnc} with its context / closure given as {@code fncContext} remotely and
   * write the result of this execution to the given promise {@promise}.
   * @param fnc is the function to execute remotely
   * @param fncContext is the context of that function. Spoken differently, it's closure. Later, Here Spores (SIP-21) comes into play
   * @param promise the promise to write the result to
   * @tparam C type of function context
   * @tparam T return value of function
   */
  override def execute[C, T](fnc: () => T, fncContext: C, promise: Promise[T]): Unit = {

    printDbg("Create new PromiseCompleting ")
    val runnable: PromiseCompletingRunnable[T] = new PromiseCompletingRunnable(fnc, promise)
    printDbg("Sending tasks to hazelcast grid")
    executor.execute(runnable)
  }

  override def finalize(): Unit = {
    super.finalize()
    hz.shutdown()
    executor.shutdown()
  }
}
