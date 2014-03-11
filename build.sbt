val scalaCheckVersion = sbt.settingKey[String]("Version to use for the scalacheck dependency.")
val scalaTestVersion = sbt.settingKey[String]("Version to use for ScalaTest.")

name := " Distributed Remote Futures"

version := "0.1"

scalaVersion := "2.11.0-RC1"

scalaTestVersion := "2.1.0"

scalaCheckVersion := "1.11.3"

scalacOptions in(Compile, compile) ++= Seq("-optimize", "-feature", "-deprecation", "-unchecked", "-Xlint")

scalaSource in Compile <<= baseDirectory(_ / "src/scala")

scalaSource in Test <<= baseDirectory(_ / "test/scala")

libraryDependencies += "org.scala-lang" % "scala-dist" % scalaVersion.value

libraryDependencies += "org.scalacheck" %% "scalacheck" % scalaCheckVersion.value withSources() withJavadoc()

libraryDependencies += "org.scalatest" % "scalatest_2.10" % scalaTestVersion.value % "test" withSources() withJavadoc()


/* Resolvers*/
resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Compass Repository" at "http://repo.compass-project.org"

resolvers += "Twitter Repository" at "http://maven.twttr.com"

resolvers += "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"

resolvers += "Maven central" at "http://repo1.maven.org/maven2/"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

