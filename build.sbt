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
enablePlugins(JavaServerAppPackaging)
maintainer := "m-doc <info@m-doc.org>"
packageSummary := description.value
packageDescription := s"See <${homepage.value.getOrElse("")}> for more information."

// deb settings
enablePlugins(DebianPlugin)
debianPackageDependencies in Debian ++= Seq("libreoffice-writer", "wkhtmltopdf", "xvfb")
serverLoading in Debian := com.typesafe.sbt.packager.archetypes.ServerLoader.SystemV

validateCommands += "debian:packageBin"
