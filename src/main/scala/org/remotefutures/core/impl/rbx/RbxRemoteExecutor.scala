/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.core.impl.rbx

import org.remotefutures.core.impl.{PromiseCompletingRunnable, RemoteExecutor}
import org.remotefutures.util.Debug._
import scala.concurrent.Promise


/**
 * @author Marvin Hansen
 */

class RbxRemoteExecutor extends RemoteExecutor {

  // switches debugging on and off
  implicit final val DBG = true
  final val executor = ???

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

    printDbg("Sending tasks to executor")
    //executor.execute(runnable)

  }
}
