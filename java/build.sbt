import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "ai.taskmonk",
      scalaVersion := "2.12.6",
      version      := "0.2"
    )),
    name := "TaskMonkSDK",
    libraryDependencies += scalaTest % Test
  )

useGpg := true
organization := "ai.taskmonk"
homepage := Some(url("https://github.com/taskmonk/taskmonkSDK"))
scmInfo := Some(ScmInfo(url("https://github.com/taskmonk/taskmonkSDK"), "git@github.com:taskmonk/taskmonkSDK"))
developers := List(Developer("taskmonk",
  "Taskmonk",
  "info@taskmonk.ai",
  url("https://github.com/taskmonk")))
licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))
publishMavenStyle := true

// Add sonatype repository settings
publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)


libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.8.0-beta2",
  "ch.qos.logback" % "logback" % "0.5",
  "com.typesafe.play" % "play-json_2.12" % "2.7.0-M1",
  "com.microsoft.azure" % "azure-servicebus" % "1.2.5" exclude("org.slf4j", "slf4j-api"),
  "org.scala-lang.modules" %% "scala-java8-compat" % "0.9.0" exclude("org.slf4j", "slf4j-api"),
  "com.softwaremill.sttp" %% "core" % "1.3.8",
  "com.softwaremill.sttp" % "play-json_2.12" % "1.3.8",
  "com.softwaremill.sttp" %% "akka-http-backend" % "1.3.8",
  "com.typesafe.akka" % "akka-stream_2.12" % "2.5.17",
  "com.google.inject" % "guice" % "4.2.1",
  "org.scala-lang.modules" % "scala-java8-compat_2.12" % "0.9.0"
)
