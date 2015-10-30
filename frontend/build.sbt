name := "java-deptools-frontend"

rpmProvides := Seq("config(java-deptools-frontend)")

libraryDependencies ++= Seq(
  "org.webjars" % "jquery" % "2.1.4",
  "org.postgresql" % "postgresql" % "9.4-1204-jdbc42"
)
