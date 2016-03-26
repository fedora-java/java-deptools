name := "java-deptools-core"

fork in run := true

libraryDependencies ++= Seq(
  "org.apache.logging.log4j" % "log4j-api" % "2.3",
  "org.apache.logging.log4j" % "log4j-core" % "2.3",
  "org.apache.commons" % "commons-compress" % "1.4.1",
  "org.fedoraproject.javadeptools" % "java-deptools-native" % "1.0.0-SNAPSHOT",
  "com.typesafe.play" %% "anorm" % "2.5.0",
  "com.github.scopt" %% "scopt" % "3.4.0",
  "com.zaxxer" % "HikariCP" % "2.4.5",
  "org.postgresql" % "postgresql" % "9.4.1208.jre7",

  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)
