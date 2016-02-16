enablePlugins(MdocPlugin)

name := "rendering-service"

libraryDependencies ++= Seq(
  MdocLibrary.commonModel,
  MdocLibrary.renderingEngines,
  Library.circeGeneric,
  Library.http4sCirce,
  Library.http4sCore,
  Library.http4sDsl,
  Library.http4sBlazeServer,
  Library.logbackClassic,
  Library.properly,
  Library.scalaLogging,
  Library.scalacheck % "test"
)

// sbt-native-packager
enablePlugins(JavaServerAppPackaging, DebianPlugin)
debianPackageDependencies in Debian ++= Seq("libreoffice-writer", "wkhtmltopdf", "xvfb")
serverLoading in Debian := com.typesafe.sbt.packager.archetypes.ServerLoader.SystemV

mdocValidateCommands += "debian:packageBin"
