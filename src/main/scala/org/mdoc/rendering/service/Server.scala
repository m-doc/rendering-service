package org.mdoc.rendering.service

import com.typesafe.scalalogging.StrictLogging
import org.http4s.server.blaze.BlazeBuilder

object Server extends App with StrictLogging {
  val server = BlazeBuilder
    .bindHttp(8081, "::")
    .mountService(Service.route)
    .start

  server.attemptRun.fold(t => {
    logger.error(t.getMessage, t)
    sys.exit(1)
  }, _.awaitShutdown())
}
