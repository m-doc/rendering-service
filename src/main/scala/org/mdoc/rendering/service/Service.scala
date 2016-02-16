package org.mdoc.rendering.service

import com.typesafe.scalalogging.StrictLogging
import io.circe.generic.auto._
import org.http4s.{ circe, HttpService, Response }
import org.http4s.dsl._
import org.mdoc.common.model.{ Document, RenderingInput }
import org.mdoc.common.model.circe._
import org.mdoc.fshell.Shell.ShellSyntax
import org.mdoc.rendering.engines.generic
import scalaz.concurrent.Task
import scalaz.syntax.bind._

object Service extends StrictLogging {
  val route = HttpService {
    case req @ POST -> Root / "render" =>
      req.decode[RenderingInput](render)(circe.jsonOf).handleWith {
        case throwable =>
          Task.delay(logger.error("POST /render", throwable)) >>
            InternalServerError(throwable.getMessage)
      }

    case GET -> Root / "version" =>
      Ok(BuildInfo.version)
  }

  def render(input: RenderingInput): Task[Response] =
    generic.renderDoc(input).runTask.flatMap(docToResponse)

  def docToResponse(doc: Document): Task[Response] =
    Ok(doc.body).withType(doc.format.toMediaType)
}
