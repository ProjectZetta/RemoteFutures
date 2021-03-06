/*
 * Copyright (c) 2014 Martin Senne
 */
package org.remotefutures.core.impl.akka.pullingworker.messages

/**
 * Execute case class. Used from caller site.
 * @param body is the code to execute on callee (remote node) site.
 */
case class Execute[T](body: () => T)

case class Work(workId: String, job: () => Any)
// case class Work(workId: String, job: Any)


case class WorkResult(workId: String, result: Any)

/** Confirmation that work has been accepted by master */
case class WorkIsAccepted(workId: String)