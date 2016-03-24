name := "java-deptools-core"

fork in run := true

libraryDependencies ++= Seq(
  "org.apache.logging.log4j" % "log4j-api" % "2.3",
  "org.apache.logging.log4j" % "log4j-core" % "2.3",
  "org.apache.commons" % "commons-compress" % "1.4.1",
  "org.fedoraproject.javadeptools" % "java-deptools-native" % "1.0.0-SNAPSHOT",
  "com.typesafe.play" %% "anorm" % "2.5.0",
  "org.scalikejdbc" %% "scalikejdbc" % "2.3.5",
  "com.github.scopt" %% "scopt" % "3.4.0"
)
