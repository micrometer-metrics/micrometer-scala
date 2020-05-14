import sbt.Keys._
import sbt._

object BuildSettings {

  lazy val common: Seq[Def.Setting[_]] = Seq(
    crossScalaVersions := List(scalaVersion.value, "2.12.11"),
    fork := true,
    libraryDependencies ++= Seq(
      compilerPlugin(Dependencies.kindProjector),
      Dependencies.catsEffect,
      Dependencies.scalaCollectionCompat,
      Dependencies.scalaTest % Test
    ),
    Test / publishArtifact := false
  )

}
