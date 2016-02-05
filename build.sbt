enablePlugins(MdocPlugin)

name := "rendering-service"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % Version.logback,
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "org.http4s" %% "http4s-core" % Version.http4s,
  "org.http4s" %% "http4s-dsl" % Version.http4s,
  "org.http4s" %% "http4s-blaze-server" % Version.http4s,
  "org.m-doc" %% "common-model" % "0.0.0",
  "org.m-doc" %% "rendering-engines" % Version.renderingEngines,
  "org.scalacheck" %% "scalacheck" % Version.scalacheck % "test"
)
