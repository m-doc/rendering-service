package org.mdoc.rendering.service

import org.http4s.{ Request, Uri }
import org.http4s.dsl._
import org.scalacheck.Prop._
import org.scalacheck.Properties

object ServiceSpec extends Properties("Service") {

  property("showRequest") = forAll { (s: String) =>
    Service.showRequest(Request(uri = Uri(path = s))) ?= s"GET $s"
  }

  property("route: /") = secure {
    Service.route.run(Request()).run.status ?= NotFound
  }

  property("route: /render/pdf/test.pdf") = secure {
    val req = Request(uri = Uri(path = "/render/pdf/test.pdf"))
    Service.route.run(req).run.status ?= BadRequest
  }

  property("route: /version") = secure {
    Service.route.run(Request(uri = Uri(path = "/version"))).run.status ?= Ok
  }
}
