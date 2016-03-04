package org.mdoc.rendering.service

import cats.data.Xor
import com.typesafe.scalalogging.StrictLogging
import io.circe.generic.auto._
import org.http4s.{ circe, HttpService, Request, Response }
import org.http4s.dsl._
import org.mdoc.common.model.jvm.FormatOps
import org.mdoc.common.model.Document
import org.mdoc.common.model.Format
import org.mdoc.common.model.Format.{ Bmp, Jpeg, Pdf, Png, Svg }
import org.mdoc.common.model.RenderingInput
import org.mdoc.common.model.circe._
import org.mdoc.fshell.Shell.ShellSyntax
import org.mdoc.rendering.engines.{ generic, wkhtmltopdf }
import scalaz.Kleisli
import scalaz.concurrent.Task
import scalaz.syntax.bind._

object Service extends StrictLogging {
  val logRequestsKleisli: Kleisli[Task, Request, Request] =
    Kleisli(req => logRequest(req).as(req))

  val renderUrlRoute: PartialFunction[Request, Task[Response]] =
    List(Bmp, Jpeg, Pdf, Png, Svg).map(renderUrlAsRoute).reduce(_ orElse _)

  val bareRoute: HttpService = HttpService {
    renderUrlRoute orElse {
      case req @ POST -> Root / "render" =>
        renderDocService(req)

      case GET -> Root / "version" =>
        Ok(BuildInfo.version)
    }
  }

  val route: HttpService = Kleisli { req =>
    logRequestsKleisli.andThen(bareRoute).run(req).handleWith(logException(req))
  }

  def docToResponse(doc: Document): Task[Response] =
    Ok(doc.body).withType(FormatOps.toMediaType(doc.format))

  def extractParam(req: Request, param: String): Xor[String, String] = {
    val message = s"""Parameter "$param" is not specified."""
    Xor.fromOption(req.params.get(param), message)
  }

  def logException(req: Request): PartialFunction[Throwable, Task[Response]] = {
    case throwable =>
      Task.delay(logger.error(showRequest(req), throwable)) >>
        InternalServerError(throwable.getMessage)
  }

  def logRequest(req: Request): Task[Unit] =
    Task.delay(logger.debug(showRequest(req)))

  def renderDoc(input: RenderingInput): Task[Response] =
    generic.renderDoc(input).runTask.flatMap(docToResponse)

  def renderDocService(req: Request): Task[Response] =
    req.decode[RenderingInput](renderDoc)(circe.jsonOf)

  def renderUrlAs(url: String, format: Format): Task[Response] =
    wkhtmltopdf.renderUrl(url, format).runTask.flatMap(docToResponse)

  def renderUrlAsRoute(format: Format): PartialFunction[Request, Task[Response]] = {
    case req @ GET -> Root / "render" / fmt / _ if fmt == format.toExtension =>
      renderUrlAsService(req, format)
  }

  def renderUrlAsService(req: Request, format: Format): Task[Response] =
    extractParam(req, "url").fold(BadRequest(_), renderUrlAs(_, format))

  def showRequest(req: Request): String =
    s"${req.method.name} ${req.uri.renderString}"
}
