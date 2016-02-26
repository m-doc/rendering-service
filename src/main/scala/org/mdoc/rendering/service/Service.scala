package org.mdoc.rendering.service

import cats.data.Xor
import com.typesafe.scalalogging.StrictLogging
import io.circe.generic.auto._
import org.http4s.{ circe, HttpService, Request, Response }
import org.http4s.dsl._
import org.mdoc.common.model.Document
import org.mdoc.common.model.Format
import org.mdoc.common.model.Format.{ Jpeg, Pdf, Png }
import org.mdoc.common.model.RenderingInput
import org.mdoc.common.model.circe._
import org.mdoc.fshell.Shell.ShellSyntax
import org.mdoc.rendering.engines.{ generic, wkhtmltopdf }
import scalaz.Kleisli
import scalaz.concurrent.Task
import scalaz.syntax.bind._

object Service extends StrictLogging {
  val loggingRoute: Kleisli[Task, Request, Request] =
    Kleisli(req => logRequest(req).as(req))

  val simpleRoute: HttpService = HttpService {
    case req @ POST -> Root / "render" =>
      endpointRender(req)

    case req @ GET -> Root / "render" / "jpg" / _ =>
      endpointRenderFormat(req, Jpeg)

    case req @ GET -> Root / "render" / "pdf" / _ =>
      endpointRenderFormat(req, Pdf)

    case req @ GET -> Root / "render" / "png" / _ =>
      endpointRenderFormat(req, Png)

    case GET -> Root / "version" =>
      Ok(BuildInfo.version)
  }

  val route: HttpService = Kleisli { req =>
    loggingRoute.andThen(simpleRoute).run(req).handleWith(logException(req))
  }

  def docToResponse(doc: Document): Task[Response] =
    Ok(doc.body).withType(doc.format.toMediaType)

  def endpointRender(req: Request): Task[Response] =
    req.decode[RenderingInput](renderDoc)(circe.jsonOf)

  def endpointRenderFormat(req: Request, format: Format): Task[Response] =
    extractParam(req, "url").fold(BadRequest(_), renderUrl(_, format))

  def extractParam(req: Request, param: String): Xor[String, String] = {
    val message = s"""Parameter "$param" is not specified."""
    Xor.fromOption(req.params.get(param), message)
  }

  def logException(req: Request): PartialFunction[Throwable, Task[Response]] = {
    case throwable =>
      Task.delay(logger.error(renderRequest(req), throwable)) >>
        InternalServerError(throwable.getMessage)
  }

  def logRequest(req: Request): Task[Unit] =
    Task.delay(logger.debug(renderRequest(req)))

  def renderDoc(input: RenderingInput): Task[Response] =
    generic.renderDoc(input).runTask.flatMap(docToResponse)

  def renderRequest(req: Request): String =
    s"${req.method.name} ${req.uri.renderString}"

  def renderUrl(url: String, format: Format): Task[Response] =
    wkhtmltopdf.renderUrl(url, format).runTask.flatMap(docToResponse)
}
