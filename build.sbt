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
enablePlugins(JavaServerAppPackaging, DebianPlugin, Jolokia)
debianPackageDependencies in Debian ++= Seq(
  "libreoffice-writer",
  "pandoc",
  "texlive",
  "wkhtmltopdf",
  "xvfb"
)
serverLoading in Debian := com.typesafe.sbt.packager.archetypes.ServerLoader.SystemV

linuxPackageMappings += mdocHomeDir.value

mdocValidateCommands += "debian:packageBin"
