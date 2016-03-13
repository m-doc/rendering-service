package org.mdoc.rendering.service

import eu.timepit.properly.Property
import eu.timepit.properly.Property.PropertySyntax
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeBuilder
import scalaz.concurrent.Task

object Blaze {
  val httpHost: Property[String] = {
    val defaultHost = "::"
    Property.getOrElse("HTTP_HOST", defaultHost)
  }

  val httpPort: Property[Int] = {
    val defaultPort = 8081
    Property.getAsIntOrElse("HTTP_PORT", defaultPort)
  }

  val serverBuilder: Property[BlazeBuilder] =
    for {
      host <- httpHost
      port <- httpPort
    } yield {
      BlazeBuilder
        .bindHttp(port, host)
        .mountService(Service.route)
    }

  val server: Task[Server] =
    serverBuilder.runTask.flatMap(_.start)
}
