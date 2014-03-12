/* Copyright (c) 2014 Marvin Hansen.
* www.marvin - hansen.tel.
  * ALl RIGHTS RESERVED
  ***************************
* Project: DistributedRemoteFutures
* User: Marvin Hansen
  * Web: www.marvin - hansen.tel
* Date: 3 / 12 / 14(/ dd / mm / yy)
* Time: 7: 35 PM (CET)
*/
package core

//** Interface */
trait RemoteExecutionContext {

  /**
   * Execute a function in its given context on a distant place.
   *
   * @param body is the code to execute and return T eventually
   * @param bodyContext is the context/closure of of function body: () => T
   * @tparam C specifies the Context type
   * @tparam T specifies the return tyoe
   */
  def execute[C, T](body: () => T, bodyContext: C): Unit

  def execute(runnable: Runnable): Unit

  def reportFailure(cause: Throwable): Unit

  def prepare(): RemoteExecutionContext = this

}

object RemoteExecutionContext {

  /** Creates an `ExecutionContext` from the given `Executor`.
    */
  def fromRemoteExecutor(e: DistributedRemoteExecutor, reporter: Throwable => Unit): RemoteExecutionContext = ???

  /** Creates an `ExecutionContext` from the given `Executor` with the default Reporter. */
  def fromRemoteExecutor(e: DistributedRemoteExecutor): RemoteExecutionContext = fromRemoteExecutor(e, defaultReporter)

  /** The default reporter simply prints the stack trace of the `Throwable` to System.err. */
  def defaultReporter: Throwable => Unit = _.printStackTrace()
}

private class RemoteExecutionContextImpl(es: DistributedRemoteExecutor, reporter: Throwable => Unit) extends RemoteExecutionContext {


  override def execute[C, T](body: () => T, bodyContext: C): Unit = ???

  override def execute(runnable: Runnable): Unit = ???

  override def reportFailure(t: Throwable) = reporter(t)

}

