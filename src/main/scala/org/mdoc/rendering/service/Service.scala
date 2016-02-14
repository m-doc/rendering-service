package org.mdoc.rendering.service

import io.circe.generic.auto._
import org.http4s.{ circe, HttpService, Response }
import org.http4s.dsl._
import org.mdoc.common.model.CompleteTemplate
import org.mdoc.common.model.circe._
import org.mdoc.common.model.RenderingEngine
import org.mdoc.fshell.Shell.ShellSyntax
import org.mdoc.rendering.engines.{ LibreOffice, Wkhtmltopdf }
import scalaz.concurrent.Task

object Service {
  val route = HttpService {
    case req @ POST -> Root / "render" =>
      req.decode[CompleteTemplate](render)(circe.jsonOf).handleWith {
        case throwable => InternalServerError(throwable.getMessage)
      }

    case GET -> Root / "version" =>
      Ok(BuildInfo.version)
  }

  // example body:
  // {"cfg":{"outputFormat":{"Pdf":{}},"engine":{"LibreOffice":{}}},"doc":{"format":{"Html":{}},"body":"SGVsbG8="}}

  def render(template: CompleteTemplate): Task[Response] = {
    template.cfg.engine match {
      case RenderingEngine.LibreOffice =>
        LibreOffice.convertTo(template.doc, template.cfg.outputFormat).runTask
          .flatMap(bs => Ok(bs.body).replaceAllHeaders(Utils.formatContentType(bs.format)))
      case RenderingEngine.Wkhtmltopdf =>
        Wkhtmltopdf.htmlToPdf(template.doc.body).runTask
          .flatMap(bs => Ok(bs.body).replaceAllHeaders(Utils.formatContentType(bs.format)))
      case _ => BadRequest("")
    }
  }
}
