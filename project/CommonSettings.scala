import sbt.Keys._
import sbt._

object CommonSettings {
  object settingValues {
    val version = "1.0.0-SNAPSHOT"

    val scalaVersion      = "2.13.1"
    val crossScalaVersion = Seq("2.12.11", "2.13.1")

    val organization = "com.igeolise.io.micrometer"
    val scalacOptions = Seq(
      "-feature",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-language:existentials",
      "-language:postfixOps",
      "-deprecation",
      "-unchecked"
    )
  }

  val defaultSettings = {
    import sbt.Keys._
    Seq(
      scalaVersion := settingValues.scalaVersion,
      crossScalaVersions := settingValues.crossScalaVersion,
      scalacOptions := settingValues.scalacOptions,
      organization := settingValues.organization,
      version := settingValues.version,
      // because of doc generation errors for methods with pureconfig parameters
      publishArtifact in (Compile, packageDoc) := false
    )
  }

  val defaultProjectSettings: Seq[Setting[_]] = {
    Seq(
      addCompilerPlugin(
        "io.tryp" % "splain" % PluginDeps.versions.splainPluginCompilerVersion cross CrossVersion.patch
      ),
      scalacOptions += "-P:splain:tree:true",
      //scalacOptions += "-P:splain:compact:true",
      scalacOptions += "-P:splain:breakinfix:1"
    ) ++ defaultSettings
  }

}
