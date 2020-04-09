import sbt._

object Dependencies {

  val catsEffect = "org.typelevel" %% "cats-effect" % "2.1.2"
  val kindProjector = "org.typelevel" % "kind-projector" % "0.11.0" cross CrossVersion.full
  val micrometerCore = "io.micrometer" % "micrometer-core" % Versions.micrometerCore
  val scalaCollectionCompat = "org.scala-lang.modules" %% "scala-collection-compat" % "2.1.4"
  val scalafixScaluzzi = "com.github.vovapolu" %% "scaluzzi" % "0.1.3"
  val scalafixSortImports = "com.nequissimus" %% "sort-imports" % "0.3.2"
  val scalaTest = "org.scalatest" %% "scalatest" % "3.1.1"
  val silencer = "com.github.ghik" % "silencer-plugin" % Versions.silencer cross CrossVersion.full
  val silencerLib = "com.github.ghik" % "silencer-lib" % Versions.silencer cross CrossVersion.full

  object Versions {

    val micrometerCore = "1.4.1"
    val silencer = "1.6.0"

  }

}
