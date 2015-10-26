lazy val commonSettings = Seq(
  organization := "org.fedoraproject",
  version := "0",
  scalaVersion := "2.11.6"
)

lazy val root = (project in file(".")).
  aggregate(core).
  settings(commonSettings: _*).
  settings(
    name := "java-deptools"
  )

lazy val core = (project in file("core")).
  settings(commonSettings: _*)

lazy val frontend = (project in file("frontend")).
  settings(commonSettings: _*).
  enablePlugins(PlayScala).
  enablePlugins(RpmPlugin).
  dependsOn(core)
