import com.typesafe.sbt.packager.archetypes.ServerLoader

name := "java-deptools-frontend"

rpmVendor := "typesafe"
rpmLicense := Some("ASL 2.0")
rpmAutoprov := "no"
rpmRequirements := Seq("java-headless")
serverLoading in Rpm := ServerLoader.Systemd

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "9.4-1204-jdbc42"
)
