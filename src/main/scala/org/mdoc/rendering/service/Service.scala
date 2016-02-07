package org.mdoc.rendering.service

import io.circe.generic.auto._
import org.http4s.{ circe, HttpService, Response }
import org.http4s.MediaType._
import org.http4s.dsl._
import org.http4s.headers.`Content-Type`
import org.mdoc.common.model.CirceInstances._
import org.mdoc.common.model.CompleteTemplate
import org.mdoc.fshell.Shell.ShellSyntax
import org.mdoc.rendering.engines.Wkhtmltopdf
import scalaz.concurrent.Task

object Service {
  val route = HttpService {
    case req @ POST -> Root / "render" =>
      req.decode[CompleteTemplate] { a =>
        // TODO: CompleteTemplate => Task[Response]
        val ret = Wkhtmltopdf.htmlToPdf(a.body)
        ret.runTask.flatMap(bs => Ok(bs).replaceAllHeaders(`Content-Type`(`application/pdf`)))
      }(circe.jsonOf)

    case GET -> Root / "version" =>
      Ok(BuildInfo.version)
  }

  def render(template: CompleteTemplate): Task[Response] = {
    // What should be the format of the response? PDF, PNG, JPG, HTML, PlainText?
    // What is the input format? Different engines can handle different input formats? ODT, DOCX, HTML, LaTeX
    // Which engine should we use? Output quality differs between engines.

    // The body of CompleteTemplate has a specific format.
    ???
  }
}
