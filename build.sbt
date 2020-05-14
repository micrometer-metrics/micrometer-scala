ThisBuild / organization := "io.micrometer"
ThisBuild / scalaVersion := "2.13.2"
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
      Dependencies.jsr305,
      Dependencies.micrometerCore
    )
  )

addCommandAlias("check", "; scalafmtSbtCheck; scalafmtCheckAll; +test")
addCommandAlias("fix", "; scalafmtSbt; scalafmtAll")
