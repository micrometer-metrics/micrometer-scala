ThisBuild / organization := "io.micrometer"
ThisBuild / scalaVersion := "2.13.1"
ThisBuild / turbo := true
Global / onChangedBuildSource := ReloadOnSourceChanges
Global / cancelable := true

lazy val root = project
  .in(file("."))
  .aggregate(api)
  .settings(
    name := "micrometer-scala",
    publish / skip := true
  )

lazy val api = project
  .in(file("api"))
  .settings(BuildSettings.common)
  .settings(
    name := "micrometer-scala-api",
    libraryDependencies ++= Seq(
      Dependencies.catsEffect,
      Dependencies.micrometerCore
    )
  )

addCommandAlias("checkAll", "; scalafmtSbtCheck; scalafmtCheckAll; compile:scalafix --check; test:scalafix --check; +test")
addCommandAlias("fixAll", "; compile:scalafix; test:scalafix; scalafmtSbt; scalafmtAll")
