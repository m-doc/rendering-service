package org.mdoc.rendering.service

import org.http4s.MediaType
import org.http4s.MediaType._
import org.http4s.headers.`Content-Type`
import org.mdoc.common.model.Format
import org.mdoc.common.model.Format._

object Utils {

  def formatContentType(format: Format): `Content-Type` =
    `Content-Type`(formatMediaType(format))

  def formatMediaType(format: Format): MediaType =
    format match {
      case Docx => `application/vnd.openxmlformats-officedocument.wordprocessingml.document`
      case Html => `text/html`
      case Jpeg => `image/jpeg`
      case Latex => `application/x-tex`
      case Odt => `application/vnd.oasis.opendocument.text`
      case Pdf => `application/pdf`
      case Png => `image/png`
    }
}
