name := """frontend"""

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "9.4-1204-jdbc42"
)
