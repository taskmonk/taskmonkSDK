import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "io.taskmonk",
      scalaVersion := "2.12.6",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "TaskMonkSDK",
    libraryDependencies += scalaTest % Test
  )

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.8.0-beta2",
  "ch.qos.logback" % "logback" % "0.5",
  "com.typesafe.play" % "play-json_2.12" % "2.7.0-M1",
  "com.microsoft.azure" % "azure-servicebus" % "1.2.5" exclude("org.slf4j", "slf4j-api"),
  "org.scala-lang.modules" %% "scala-java8-compat" % "0.9.0" exclude("org.slf4j", "slf4j-api")
)