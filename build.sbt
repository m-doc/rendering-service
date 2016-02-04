enablePlugins(MdocPlugin)

name := "rendering-service"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-core" % Version.http4s,
  "org.http4s" %% "http4s-dsl" % Version.http4s,
  "org.http4s" %% "http4s-blaze-server" % Version.http4s,
  "org.m-doc" %% "common-model" % "0.0.0",
  "org.m-doc" %% "rendering-engines" % Version.renderingEngines,
  "org.scalacheck" %% "scalacheck" % Version.scalacheck % "test"
)
