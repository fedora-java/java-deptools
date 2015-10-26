name := "java-deptools-frontend"

rpmVendor := "typesafe"
rpmLicense := Some("ASL 2.0")
rpmAutoprov := "no"
rpmRequirements := Seq("java-headless")

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "9.4-1204-jdbc42"
)
