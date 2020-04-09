import sbt.Keys._
import sbt._
import scalafix.sbt.ScalafixPlugin.autoImport._

object BuildSettings {

  lazy val common: Seq[Def.Setting[_]] = Seq(
    crossScalaVersions := List(scalaVersion.value, "2.12.10"),
    fork := true,
    libraryDependencies ++= Seq(
      compilerPlugin(Dependencies.kindProjector),
      compilerPlugin(Dependencies.silencer),
      compilerPlugin(scalafixSemanticdb), // necessary for Scalafix
      Dependencies.silencerLib,
      Dependencies.catsEffect,
      Dependencies.scalaCollectionCompat,
      Dependencies.scalaTest % Test
    ),
    ThisBuild / scalafixDependencies ++= Seq(
      Dependencies.scalafixScaluzzi,
      Dependencies.scalafixSortImports
    ),
    scalacOptions ++= Seq(
      "-Yrangepos", // necessary for Scalafix (required by SemanticDB compiler plugin)
      "-Ywarn-unused", // necessary for Scalafix RemoveUnused rule (not present in sbt-tpolecat for 2.13)
      "-P:silencer:checkUnused"
    ),
    Test / publishArtifact := false
  )

}
