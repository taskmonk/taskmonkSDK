import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "ai.taskmonk",
      scalaVersion := "2.12.6",
      version      := "0.6-SNAPSHOT"
    )),
    name := "TaskMonkSDK",
    libraryDependencies += scalaTest % Test
  )

useGpg := true
ThisBuild / organization := "ai.taskmonk"
ThisBuild / homepage := Some(url("https://github.com/taskmonk/taskmonkSDK"))
ThisBuild / scmInfo := Some(ScmInfo(url("https://github.com/taskmonk/taskmonkSDK"), "git@github.com:taskmonk/taskmonkSDK"))
ThisBuild / developers := List(Developer("taskmonk",
  "Taskmonk",
  "info@taskmonk.ai",
  url("https://github.com/taskmonk")))
ThisBuild / licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))
ThisBuild / publishMavenStyle := true

// Add sonatype repository settings
ThisBuild / publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)


libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.8.0-beta2",
//  "ch.qos.logback" % "logback" % "1.2.3",
  "ch.qos.logback" % "logback-classic" % "1.3.0-alpha4",

"com.typesafe.play" % "play-json_2.12" % "2.7.0-M1",
  "com.microsoft.azure" % "azure-servicebus" % "1.2.5" exclude("org.slf4j", "slf4j-api"),
  "org.scala-lang.modules" %% "scala-java8-compat" % "0.9.0" exclude("org.slf4j", "slf4j-api"),
  "com.typesafe.play" %% "play-ahc-ws-standalone" % "2.0.1",
  "com.softwaremill.sttp" %% "core" % "1.3.8",
  "com.softwaremill.sttp" % "play-json_2.12" % "1.3.8",
  "com.softwaremill.sttp" %% "akka-http-backend" % "1.3.8",
  "com.typesafe.akka" % "akka-stream_2.12" % "2.5.17",
  "com.google.inject" % "guice" % "4.2.1",
  "org.scala-lang.modules" % "scala-java8-compat_2.12" % "0.9.0"
)
