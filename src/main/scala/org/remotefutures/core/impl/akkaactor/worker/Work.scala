package org.remotefutures.core.impl.akkaactor.worker

case class Work(workId: String, job: Any)

case class WorkResult(workId: String, result: Any)