enablePlugins(MdocPlugin)

name := "rendering-service"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % Version.logback,
  "com.typesafe.scala-logging" %% "scala-logging" % Version.scalaLogging,
  "eu.timepit" %% "properly" % Version.properly,
  "io.circe" %% "circe-generic" % "0.2.0",
  "org.http4s" %% "http4s-circe" % Version.http4s,
  "org.http4s" %% "http4s-core" % Version.http4s,
  "org.http4s" %% "http4s-dsl" % Version.http4s,
  "org.http4s" %% "http4s-blaze-server" % Version.http4s,
  "org.m-doc" %% "common-model" % Version.commonModel,
  "org.m-doc" %% "rendering-engines" % Version.renderingEngines,
  "org.scalacheck" %% "scalacheck" % Version.scalacheck % "test"
)

// sbt-native-packager
enablePlugins(JavaServerAppPackaging)
maintainer := "m-doc <info@m-doc.org>"
packageSummary := description.value
packageDescription := s"See <${homepage.value.getOrElse("")}> for more information."

// deb settings
enablePlugins(DebianPlugin)
debianPackageDependencies in Debian ++= Seq("wkhtmltopdf", "xvfb")
serverLoading in Debian := com.typesafe.sbt.packager.archetypes.ServerLoader.SystemV

validateCommands += "debian:packageBin"
