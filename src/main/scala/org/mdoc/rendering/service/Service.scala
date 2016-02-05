package org.mdoc.rendering.service

import io.circe.generic.auto._
import org.http4s.HttpService
import org.http4s.circe
import org.http4s.dsl._
import org.mdoc.common.model.FilledOutTemplate

object Service {
  val route = HttpService {
    case req @ POST -> Root / "render" =>
      req.decode[FilledOutTemplate](a => Ok(a.toString))(circe.jsonOf)

    case GET -> Root / "version" =>
      Ok(BuildInfo.version)
  }
}
