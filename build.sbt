import com.typesafe.sbt.packager.archetypes.ServerLoader

lazy val commonSettings = Seq(
  organization := "org.fedoraproject",
  version := "0",
  scalaVersion := "2.11.6",
  rpmVendor := "typesafe",
  rpmLicense := Some("ASL 2.0"),
  rpmAutoprov := "no",
  rpmRequirements := Seq("java-headless"),
  rpmProvides := Seq("config(java-deptools)"),
  serverLoading in Rpm := ServerLoader.Systemd
)

lazy val root = (project in file(".")).
  aggregate(core, frontend).
  settings(commonSettings: _*).
  settings(
    name := "java-deptools"
  ).
  enablePlugins(RpmPlugin)

lazy val core = (project in file("core")).
  settings(commonSettings: _*).
  enablePlugins(JavaAppPackaging)

lazy val frontend = (project in file("frontend")).
  settings(commonSettings: _*).
  enablePlugins(PlayScala).
  dependsOn(core)
