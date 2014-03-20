import sbt._
import sbt.Keys._

import com.typesafe.sbt.SbtMultiJvm
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm


object BuildSettings {
  val buildSettings = Defaults.defaultSettings ++ Seq(
    name := " Distributed Remote Futures",
    organization := "org.remotefutures",
    version := "0.0.1",
    scalaVersion := "2.10.4",

    scalacOptions ++= Seq()
  )
}

object Resolvers {

  val sonatypeReleases = Resolver.sonatypeRepo("releases")
  val typesafeResolver = "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
  val compassResolver = "Compass Repository" at "http://repo.compass-project.org"
  val twitterResolver = "Twitter Repository" at "http://maven.twttr.com"
  val maven2Resolver = "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"
  val mavenCentralResolver = "Maven central" at "http://repo1.maven.org/maven2/"

  val allResolvers = Seq (sonatypeReleases, typesafeResolver, compassResolver,twitterResolver, maven2Resolver, mavenCentralResolver)
}

object Dependencies {
  val scalaTestVersion = "2.1.0"
  val scalaCheckVersion = "1.11.3"
  val hazelVersion = "3.2-RC1"
  val akkaVersion = "2.3.0"

  val scalaCheck = "org.scalacheck" %% "scalacheck" % scalaCheckVersion withSources() withJavadoc()
  val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVersion % "test" withSources() withJavadoc()

  val config = "com.typesafe" % "config" % "1.2.0" withSources() withJavadoc()

  val hazelcast = "com.hazelcast" % "hazelcast" % hazelVersion withSources() withJavadoc()

  val akkaActor = "com.typesafe.akka" %% "akka-actor" % "2.3.0" withSources() withJavadoc()
  val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % "2.3.0" withSources() withJavadoc()
  val akkaCluster = "com.typesafe.akka" %% "akka-cluster" % "2.3.0" withSources() withJavadoc()

  val allDeps = Seq( scalaCheck, scalaTest, config, hazelcast, akkaActor, akkaTestkit, akkaCluster )
}

object MyBuild extends Build {
  import BuildSettings._
  import Dependencies._
  import Resolvers._

  lazy val root: Project = Project(
    "root",
    file("."),
    settings = buildSettings ++ SbtMultiJvm.multiJvmSettings
      ++ Seq (resolvers := allResolvers)
      ++ Seq(
        libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-reflect" % _),
        libraryDependencies ++= (
          if (scalaVersion.value.startsWith("2.10")) List("org.scalamacros" % "quasiquotes" % "2.0.0-M3" cross CrossVersion.full)
          else Nil
        ))
      ++ Seq (libraryDependencies ++= allDeps )
      ++ Seq (libraryDependencies ++= Seq(
        "com.typesafe.akka" %% "akka-remote" % akkaVersion,
        "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion
      ))
      ++ Seq(
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
      })
  ) configs (MultiJvm)


  // ++ Seq( run <<= run in Compile in core )
  // aggregate(macros, core)



//  lazy val macros: Project = Project(
//    "macros",
//    file("macros"),
//    settings = buildSettings ++ Seq(
//      libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-reflect" % _),
//      libraryDependencies ++= (
//        if (scalaVersion.value.startsWith("2.10")) List("org.scalamacros" % "quasiquotes" % "2.0.0-M3" cross CrossVersion.full)
//        else Nil
//      ))
//  )
//
//  lazy val core: Project = Project(
//    "core",
//    file("core"),
//    settings = buildSettings
//  ) dependsOn(macros)




//  val project = Project(
//    id = "multi-node-scala",
//    base = file("multinode."),
//    settings = Project.defaultSettings ++ SbtMultiJvm.multiJvmSettings ++ Seq(
//      name := "multi-node-scala",
//      version := "1.0",
//      //scalaVersion := "2.10.3",
//      libraryDependencies ++= Seq(
//        "com.typesafe.akka" %% "akka-remote" % akkaVersion,
//        "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion
//      ),
//        // "org.scalatest" %% "scalatest" % "2.0" % "test"),
//      // make sure that MultiJvm test are compiled by the default test compilation
//      compile in MultiJvm <<= (compile in MultiJvm) triggeredBy (compile in Test),
//      // disable parallel tests
//      parallelExecution in Test := false,
//      // make sure that MultiJvm tests are executed by the default test target,
//      // and combine the results from ordinary test and multi-jvm tests
//      executeTests in Test <<= (executeTests in Test, executeTests in MultiJvm) map {
//        case (testResults, multiNodeResults) =>
//          val overall =
//            if (testResults.overall.id < multiNodeResults.overall.id)
//              multiNodeResults.overall
//            else
//              testResults.overall
//          Tests.Output(overall,
//            testResults.events ++ multiNodeResults.events,
//            testResults.summaries ++ multiNodeResults.summaries)
//      }
//    )
//  ) configs (MultiJvm)
}