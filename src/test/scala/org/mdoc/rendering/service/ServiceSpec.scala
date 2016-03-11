package org.mdoc.rendering.service

import org.http4s.{ Request, Uri }
import org.http4s.dsl._
import org.http4s.headers.`Content-Type`
import org.http4s.util.CaseInsensitiveString
import org.mdoc.common.model.{ Document, Format }
import org.mdoc.common.model.jvm.FormatOps
import org.scalacheck.Prop._
import org.scalacheck.Properties
import scodec.bits.ByteVector

object ServiceSpec extends Properties("Service") {

  property("docToResponse sets ContentType") = secure {
    Format.values.forall { format =>
      val doc = Document(format, ByteVector.view("".getBytes))
      val response = Service.docToResponse(doc).run

      response.headers.get(CaseInsensitiveString("content-type"))
        .contains(`Content-Type`(FormatOps.toMediaType(format)))
    }
  }

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
