name := "core"

fork in run := true

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

libraryDependencies ++= Seq(
  "commons-cli" % "commons-cli" % "1.2",
  "org.apache.commons" % "commons-compress" % "1.4.1",
  "commons-lang" % "commons-lang" % "2.6",
  "bcel" % "bcel" % "5.1",
  "com.h2database" % "h2" % "1.4.187",
  "org.hibernate" % "hibernate-core" % "5.0.2.Final",
  "org.hibernate" % "hibernate-entitymanager" % "5.0.2.Final",
  "org.hibernate" % "hibernate-jpamodelgen" % "5.0.2.Final",
  "junit" % "junit" % "4.12" % "test",
  "com.novocode" % "junit-interface" % "0.10" % "test",
  "com.google.inject" % "guice" % "4.0",
  "com.google.inject.extensions" % "guice-persist" % "4.0",
  "org.fedoraproject.javadeptools" % "java-deptools-native" % "1.0.0-SNAPSHOT"
)
