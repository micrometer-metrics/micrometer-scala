import sbt._

object Dependencies {

  val catsEffect = "org.typelevel" %% "cats-effect" % "2.1.3"
  val jsr305 = "com.google.code.findbugs" % "jsr305" % "3.0.2"
  val kindProjector = "org.typelevel" % "kind-projector" % "0.11.0" cross CrossVersion.full
  val micrometerCore = "io.micrometer" % "micrometer-core" % "1.5.1"
  val scalaCollectionCompat = "org.scala-lang.modules" %% "scala-collection-compat" % "2.1.6"
  val scalaTest = "org.scalatest" %% "scalatest" % "3.1.2"

}
