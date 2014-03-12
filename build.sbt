val scalaCheckVersion = sbt.settingKey[String]("Version to use for the scalacheck dependency.")
val scalaTestVersion = sbt.settingKey[String]("Version to use for ScalaTest.")
val hazelVersion = sbt.settingKey[String]("Version to use for hazelcast dependency.")


name := " Distributed Remote Futures"

version := "0.1"

scalaVersion := "2.11.0-RC1"

scalaTestVersion := "2.1.0"

scalaCheckVersion := "1.11.3"

hazelVersion := "3.2-RC1"

scalacOptions in(Compile, compile) ++= Seq("-optimize", "-feature", "-deprecation", "-unchecked", "-Xlint")

scalaSource in Compile <<= baseDirectory(_ / "src/scala")

scalaSource in Test <<= baseDirectory(_ / "test/scala")

libraryDependencies += "org.scala-lang" % "scala-dist" % scalaVersion.value

// scala pickling for serialization
libraryDependencies += "org.scala-lang.macro-paradise" % "scala-pickling_2.11" % "0.8.0-SNAPSHOT" withSources() withJavadoc()

libraryDependencies += "org.scalacheck" %% "scalacheck" % scalaCheckVersion.value withSources() withJavadoc()

libraryDependencies += "org.scalatest" % "scalatest_2.10" % scalaTestVersion.value % "test" withSources() withJavadoc()

libraryDependencies += "com.hazelcast" % "hazelcast" % hazelVersion.value withSources() withJavadoc()

// ========= Akka dependencies
// As of http://www.scala-lang.org/news/2014/03/06/release-notes-2.11.0-RC1.html (Available projects)
// "NOTE: RC1 ships with akka-actor 2.3.0-RC4 (the final is out now, but wasn’t yet available when RC1 was cut). The next Scala 2.11 RC will ship with akka-actor 2.3.0 final."
// thus we need to include akka-cluster 2.3.0-RC, instead of the already released version Akka 2.3.0

libraryDependencies ++= Seq(
  // not needed, but here for completeness: "com.typesafe.akka" %% "akka-actor" % "2.3.0",
  //
  // Activate these, once Scala 2.11.0 is available
  // "com.typesafe.akka" %% "akka-testkit" % "2.3.0",
  // "com.typesafe.akka" %% "akka-cluster" % "2.3.0",
  "com.typesafe.akka" %% "akka-cluster" % "2.3.0-RC4"
)
// ========= Akka dependencies (end)


/* Resolvers*/
resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Compass Repository" at "http://repo.compass-project.org"

resolvers += "Twitter Repository" at "http://maven.twttr.com"

resolvers += "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"

resolvers += "Maven central" at "http://repo1.maven.org/maven2/"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

