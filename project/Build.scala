import sbt._
import sbt.Keys._


object BuildSettings {
  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "org.remotefutures",
    version := "0.0.1",
    // scalaVersion := "2.10.4",
    scalaVersion := "2.11.1",

    // scalacOptions in(Compile, compile) ++= Seq("-optimize", "-feature", "-deprecation", "-unchecked", "-Xlint")
    //scalacOptions ++= Seq()

    scalacOptions ++= Seq(
      // "-deprecation",
      // "-feature",
      // "-unchecked",
      // "-Xlint",
      // "-Xlog-reflective-calls",
      // "-Ywarn-adapted-args",
      // "-encoding", "UTF-8",
      // "-target:jvm-1.6",
      "-Ymacro-debug-lite"
    )
  )
}

object Resolvers {
  val sonatypeReleases = Resolver.sonatypeRepo("releases")
  val typesafeResolver = "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
  val compassResolver = "Compass Repository" at "http://repo.compass-project.org"
  val twitterResolver = "Twitter Repository" at "http://maven.twttr.com"
  val maven2Resolver = "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"
  val mavenCentralResolver = "Maven central" at "http://repo1.maven.org/maven2/"

  val allResolvers = Seq(sonatypeReleases, typesafeResolver, compassResolver, twitterResolver, maven2Resolver, mavenCentralResolver)
}

<<<<<<< HEAD
=======
object Dependencies {
  // val scalaTestVersion = "2.1.0"
  val scalaTestVersion = "2.1.7"
  val scalaCheckVersion = "1.11.3"
  val hazelVersion = "3.2-RC1"
  // val akkaVersion = "2.3.1"
  val akkaVersion = "2.3.3"

  val scalaAsync = "org.scala-lang.modules" %% "scala-async" % "0.9.1"

  val scalaCheck = "org.scalacheck" %% "scalacheck" % scalaCheckVersion withSources() withJavadoc()
  val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVersion % "test" withSources() withJavadoc()
>>>>>>> 5c3d758480b629bf82550a8463a05fd709e52247

object Dependencies {

<<<<<<< HEAD
  object Version {
    val ScalaTest = "2.1.0"
    val ScalaCheck = "1.11.3"
    val HazelCast = "3.2-RC1"
    val Akka = "2.3.1"
    val Config = "1.2.0"
    // for benchmark
    val Jxl = "2.6.12"
    val XStream = "1.4.4"
    val Commons = "2.6"
    val ComCol = "4.0"
    val Json = "1.0.9"
  }

  val scalaCheck = "org.scalacheck" %% "scalacheck" % Version.ScalaCheck withSources() withJavadoc()
  val scalaTest = "org.scalatest" %% "scalatest" % Version.ScalaTest % "test" withSources() withJavadoc()
  val config = "com.typesafe" % "config" % Version.Config withSources() withJavadoc()
  val hazelcast = "com.hazelcast" % "hazelcast" % Version.HazelCast withSources() withJavadoc()
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % Version.Akka withSources() withJavadoc()
  val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % Version.Akka withSources() withJavadoc()
  val akkaCluster = "com.typesafe.akka" %% "akka-cluster" % Version.Akka withSources() withJavadoc()
  val akkaRemote = "com.typesafe.akka" %% "akka-remote" % Version.Akka withSources() withJavadoc()
  val akkaMultiNode = "com.typesafe.akka" %% "akka-multi-node-testkit" % Version.Akka withSources() withJavadoc()
  val akkaContrib = "com.typesafe.akka" %% "akka-contrib" % Version.Akka withSources() withJavadoc()
=======
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion withSources() withJavadoc()
  val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion withSources() withJavadoc()
  val akkaCluster = "com.typesafe.akka" %% "akka-cluster" % akkaVersion withSources() withJavadoc()
  val akkaRemote = "com.typesafe.akka" %% "akka-remote" % akkaVersion withSources() withJavadoc()
  val akkaMultiNode = "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion withSources() withJavadoc()
  val akkaContrib = "com.typesafe.akka" %% "akka-contrib" % akkaVersion withSources() withJavadoc()
>>>>>>> 5c3d758480b629bf82550a8463a05fd709e52247

  // benchmark dependencies
  val jxl = "net.sourceforge.jexcelapi" % "jxl" % Version.Jxl withSources() withJavadoc()
  val xstream = "com.thoughtworks.xstream" % "xstream" % Version.XStream withSources() withJavadoc()
  val commons = "commons-lang" % "commons-lang" % Version.Commons withSources() withJavadoc()
  val comCol = "org.apache.commons" % "commons-collections4" % Version.ComCol withSources() withJavadoc()
  val json = "net.minidev" % "json-smart" % Version.Json withSources() withJavadoc()

  lazy val benchDeps = Seq(scalaCheck, scalaTest, jxl, xstream, commons, comCol, json)
  lazy val allDeps = Seq(scalaAsync, scalaCheck, scalaTest, config, hazelcast, akkaActor, akkaTestkit, akkaCluster, akkaRemote, akkaMultiNode, akkaContrib)
}

object MyBuild extends Build {

  import BuildSettings._
  import Dependencies._
  import Resolvers._

  import com.typesafe.sbt.SbtMultiJvm
  import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm


  /**
   * =================================================
   * Root project
   * =================================================
   */
  lazy val root: Project = Project(
    "root",
    file("."),
    settings = buildSettings ++ SbtMultiJvm.multiJvmSettings ++
      Seq(resolvers := allResolvers) ++
      Seq(libraryDependencies ++= allDeps) ++
      Seq(
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
  ) dependsOn (macros) configs (MultiJvm)


  // left as a remainder .....
  //
  // ++ Seq( run <<= run in Compile in core )
  // aggregate(macros, core)


  /**
   * =================================================
   * Macros project
   * =================================================
   */
  lazy val macros: Project = Project(
    "macros",
    file("macros"),
    settings = buildSettings ++ Seq(
      libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-reflect" % _),
      libraryDependencies ++= (
        if (scalaVersion.value.startsWith("2.10")) List("org.scalamacros" % "quasiquotes" % "2.0.0-M3" cross CrossVersion.full)
        else Nil
        ))
  )
  /**
   * =================================================
   * Benchmark project
   * =================================================
   */
  lazy val benchmark: Project = Project(
    "benchmark",
    file("benchmark"),
    settings = buildSettings ++ Seq(resolvers := allResolvers) ++
      Seq(libraryDependencies ++= benchDeps)
  )
}