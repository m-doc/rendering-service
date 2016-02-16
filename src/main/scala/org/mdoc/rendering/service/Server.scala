package org.mdoc.rendering.service

import com.typesafe.scalalogging.StrictLogging
import eu.timepit.properly.Property
import eu.timepit.properly.Property.PropertySyntax
import org.http4s.server.blaze.BlazeBuilder
import scala.util.Try

object Server extends App with StrictLogging {
  val httpPort = Property.getAsIntOrElse("HTTP_PORT", 8081).runTask

  val server = httpPort.flatMap { port =>
    BlazeBuilder
      .bindHttp(port, "::")
      .mountService(Service.route)
      .start
  }

  Try(server.run.awaitShutdown()).recover {
    case throwable =>
      logger.error("awaitShutdown()", throwable)
      sys.exit(1)
  }
}
