package org.remotefutures.util

import com.typesafe.config.Config
import scala.concurrent.duration.{Duration, FiniteDuration}
import java.util.concurrent.TimeUnit

/**
 *
 */
object Helpers {
  final implicit class ConfigOps(val config: Config) extends AnyVal {
    def getMillisDuration(path: String): FiniteDuration = getDuration(path, TimeUnit.MILLISECONDS)

    def getNanosDuration(path: String): FiniteDuration = getDuration(path, TimeUnit.NANOSECONDS)

    private def getDuration(path: String, unit: TimeUnit): FiniteDuration =
      Duration(config.getDuration(path, unit), unit)
  }
}
