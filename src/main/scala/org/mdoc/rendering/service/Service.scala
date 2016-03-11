package org.mdoc.rendering.service

import cats.data.Xor
import com.typesafe.scalalogging.StrictLogging
import io.circe.generic.auto._
import org.http4s.{ circe, HttpService, Request, Response }
import org.http4s.dsl._
import org.mdoc.common.model.jvm.FormatOps
import org.mdoc.common.model.Document
import org.mdoc.common.model.Format
import org.mdoc.common.model.Format._
import org.mdoc.common.model.RenderingInput
import org.mdoc.common.model.circe._
import org.mdoc.fshell.Shell.ShellSyntax
import org.mdoc.rendering.engines.{ generic, wkhtmltopdf }
import scalaz.Kleisli
import scalaz.concurrent.Task
import scalaz.syntax.bind._

object Service extends StrictLogging {
  type Endpoint = Request => Task[Response]
  type Route = PartialFunction[Request, Task[Response]]

  val bareRoute: HttpService = HttpService {
    val formats = List(Bmp, Jpeg, Pdf, Png, Svg)
    val formatsRoute = formats.map(renderUrlRoute).reduce(_ orElse _)

    formatsRoute orElse {
      case req @ POST -> Root / "render" =>
        renderDocEndpoint(req)

      case GET -> Root / "version" =>
        Ok(BuildInfo.version)
    }
  }

  // impure
  val route: HttpService = Kleisli { req =>
    (logRequest(req) >> bareRoute.run(req)).handleWith(logException(req))
  }

  def docToResponse(doc: Document): Task[Response] =
    Ok(doc.body).withType(FormatOps.toMediaType(doc.format))

  def extractParam(req: Request, param: String): Xor[String, String] = {
    val message = s"""Parameter "$param" is not specified."""
    Xor.fromOption(req.params.get(param), message)
  }

  // impure
  def logException(req: Request): PartialFunction[Throwable, Task[Response]] = {
    case throwable =>
      Task.delay(logger.error(showRequest(req), throwable)) >>
        InternalServerError(throwable.getMessage)
  }

  // impure
  def logRequest(req: Request): Task[Unit] =
    Task.delay(logger.debug(showRequest(req)))

  def renderDoc(input: RenderingInput): Task[Response] =
    generic.renderDoc(input).runTask.flatMap(docToResponse)

  def renderDocEndpoint: Endpoint =
    _.decode[RenderingInput](renderDoc)(circe.jsonOf)

  def renderUrl(url: String, format: Format): Task[Response] =
    wkhtmltopdf.renderUrl(url, format).runTask.flatMap(docToResponse)

  def renderUrlEndpoint(format: Format): Endpoint =
    req => extractParam(req, "url").fold(BadRequest(_), renderUrl(_, format))

  def renderUrlRoute(format: Format): Route = {
    case req @ GET -> Root / "render" / fmt / _ if fmt == format.toExtension =>
      renderUrlEndpoint(format)(req)
  }

  def showRequest(req: Request): String =
    s"${req.method.name} ${req.uri.renderString}"
}
