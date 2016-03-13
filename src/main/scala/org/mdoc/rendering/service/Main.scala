package org.mdoc.rendering.service

import com.typesafe.scalalogging.StrictLogging
import scala.util.Try

object Main extends App with StrictLogging {
  Try(Blaze.server.run.awaitShutdown()).recover {
    case throwable =>
      logger.error("awaitShutdown()", throwable)
      sys.exit(1)
  }
}
