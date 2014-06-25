package org.remotefutures.core.impl.akka.pullingworker_final

case class Work(workId: String, job: Any)

case class WorkResult(workId: String, result: Any)