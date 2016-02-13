package org.mdoc.rendering.service

import org.http4s.headers.`Content-Type`
import org.mdoc.common.model.Format

object Utils {

  def formatContentType(format: Format): `Content-Type` =
    `Content-Type`(format.toMediaType)
}
