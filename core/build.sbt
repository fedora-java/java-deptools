name := "core"

fork in run := true

libraryDependencies ++= Seq(
  "commons-cli" % "commons-cli" % "1.2",
  "org.apache.commons" % "commons-compress" % "1.4.1",
  "commons-lang" % "commons-lang" % "2.6",
  "bcel" % "bcel" % "5.1",
  "com.h2database" % "h2" % "1.4.187",
  "org.hibernate" % "hibernate-core" % "5.0.0.Beta2",
  "org.hibernate" % "hibernate-entitymanager" % "5.0.0.Beta2",
  "junit" % "junit" % "4.12"
)
