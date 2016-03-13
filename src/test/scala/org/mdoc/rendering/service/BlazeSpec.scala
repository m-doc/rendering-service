package org.mdoc.rendering.service

import eu.timepit.properly.Property.PropertySyntax
import org.scalacheck.Prop._
import org.scalacheck.Properties

object BlazeSpec extends Properties("Blaze") {

  property("serverBuilder") = secure {
    val props = Map("HTTP_HOST" -> "1.2.3.4", "HTTP_PORT" -> "1234")
    Blaze.serverBuilder.runMock(props)
    true
  }

  property("server") = secure {
    val server = Blaze.server
    true
  }
}
