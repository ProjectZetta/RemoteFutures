import com.typesafe.sbt.SbtMultiJvm
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm

name := " Distributed Remote Futures"

version := "0.1"

// scalaVersion := "2.11.0-RC1"
scalaVersion := "2.10.4"

val scalaCheckVersion = sbt.settingKey[String]("Version to use for the scalacheck dependency.")
val scalaTestVersion = sbt.settingKey[String]("Version to use for ScalaTest.")
val hazelVersion = sbt.settingKey[String]("Version to use for hazelcast dependency.")

scalaTestVersion := "2.1.0"

scalaCheckVersion := "1.11.3"

hazelVersion := "3.2-RC1"

// val akkaVersion = "2.3.0-RC4"
val akkaVersion = "2.3.0"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.0.0-M3" cross CrossVersion.full)

scalacOptions in(Compile, compile) ++= Seq("-optimize", "-feature", "-deprecation", "-unchecked", "-Xlint")

/*
 * =====================================================================================================================
 * Dependencies
 * =====================================================================================================================
 */
// For Scala 2.11.0-RC1
//libraryDependencies += "org.scala-lang" % "scala-dist" % scalaVersion.value
//
//libraryDependencies += "org.scala-lang.macro-paradise" % "scala-reflect" % "2.11.0-SNAPSHOT"
//
//
libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-reflect" % _)

libraryDependencies ++= (
  if (scalaVersion.value.startsWith("2.10")) List("org.scalamacros" % "quasiquotes" % "2.0.0-M3" cross CrossVersion.full)
  else Nil
)

//
// scala pickling for serialization
//libraryDependencies += "org.scala-lang.macro-paradise" % "scala-pickling_2.11" % "0.8.0-SNAPSHOT" withSources() withJavadoc()
//
libraryDependencies += "org.scalacheck" %% "scalacheck" % scalaCheckVersion.value withSources() withJavadoc()

libraryDependencies += "org.scalatest" %% "scalatest" % scalaTestVersion.value % "test" withSources() withJavadoc()

libraryDependencies += "com.typesafe" % "config" % "1.2.0" withSources() withJavadoc()

libraryDependencies += "com.hazelcast" % "hazelcast" % hazelVersion.value withSources() withJavadoc()

// ========= Akka dependencies
// As of http://www.scala-lang.org/news/2014/03/06/release-notes-2.11.0-RC1.html (Available projects)
// "NOTE: RC1 ships with akka-actor 2.3.0-RC4 (the final is out now, but wasn’t yet available when RC1 was cut). The next Scala 2.11 RC will ship with akka-actor 2.3.0 final."
// thus we need to include akka-cluster 2.3.0-RC, instead of the already released version Akka 2.3.0

// for ScalaVersion >= 2.10.x
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.0"

libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.3.0"

libraryDependencies += "com.typesafe.akka" %% "akka-cluster" % "2.3.0"


// for ScalaVersion >= 2.11.0-RC1
// libraryDependencies += "com.typesafe.akka" %% "akka-cluster" % "2.3.0-RC4"

// for ScalaVersion == 2.11.0 (final)
//
//libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.3.0"
//
//libraryDependencies += "com.typesafe.akka" %% "akka-cluster" % "2.3.0"
//
// ========= Akka dependencies (end)


/*
 * =====================================================================================================================
 * Resolvers
 * =====================================================================================================================
 */
resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Compass Repository" at "http://repo.compass-project.org"

resolvers += "Twitter Repository" at "http://maven.twttr.com"

resolvers += "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"

resolvers += "Maven central" at "http://repo1.maven.org/maven2/"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += Resolver.sonatypeRepo("releases")

val project = Project(
  id = "akka-sample-multi-node-scala",
  base = file("."),
  settings = Project.defaultSettings ++ SbtMultiJvm.multiJvmSettings ++ Seq(
    name := "akka-sample-multi-node-scala",
    version := "1.0",
    //scalaVersion := "2.10.3",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-remote" % akkaVersion,
      "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion
    ),
      // "org.scalatest" %% "scalatest" % "2.0" % "test"),
    // make sure that MultiJvm test are compiled by the default test compilation
    compile in MultiJvm <<= (compile in MultiJvm) triggeredBy (compile in Test),
    // disable parallel tests
    parallelExecution in Test := false,
    // make sure that MultiJvm tests are executed by the default test target,
    // and combine the results from ordinary test and multi-jvm tests
    executeTests in Test <<= (executeTests in Test, executeTests in MultiJvm) map {
      case (testResults, multiNodeResults) =>
        val overall =
          if (testResults.overall.id < multiNodeResults.overall.id)
            multiNodeResults.overall
          else
            testResults.overall
        Tests.Output(overall,
          testResults.events ++ multiNodeResults.events,
          testResults.summaries ++ multiNodeResults.summaries)
    }
  )
) configs (MultiJvm)
