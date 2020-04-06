import sbt._

object PluginDeps {
  object versions {
    val sbtNativePackagerVersion    = "1.7.0"
    val scalafmtVersion             = "2.0.1"
    val splainPluginCompilerVersion = "0.5.1"

  }

  val sbtNativePackager = addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % versions.sbtNativePackagerVersion)
  val scalafmt          = addSbtPlugin("org.scalameta"    % "sbt-scalafmt"        % versions.scalafmtVersion)
}

object CommonDeps {

  object versions {
    val scalaCollectionCompatVersion = "2.1.4"
    val scalaJava8CompatVersion      = "0.9.1"

    val micrometerVersion          = "1.4.1"
    val micrometerJvmExtrasVersion = "0.2.0"

    //val configTypesafeVersion = "1.4.0"
    val pureconfigVersion   = "0.12.3"
    val scalaLoggingVersion = "3.9.2"
    val logbackVersion      = "1.2.3"

    val okHttpVersion = "4.4.1"

    val scalaTestVersion = "3.1.1"
  }

  val scalaCollectionCompat = "org.scala-lang.modules" %% "scala-collection-compat" % versions.scalaCollectionCompatVersion
  val scalaJava8Compat      = "org.scala-lang.modules" %% "scala-java8-compat"      % versions.scalaJava8CompatVersion

  val micrometerCore       = "io.micrometer"       % "micrometer-core"                % versions.micrometerVersion
  val micrometerOpenTSDB   = "io.micrometer"       % "micrometer-registry-opentsdb"   % versions.micrometerVersion
  val micrometerPrometheus = "io.micrometer"       % "micrometer-registry-prometheus" % versions.micrometerVersion
  val micrometerJvmExtras  = "io.github.mweirauch" % "micrometer-jvm-extras"          % versions.micrometerJvmExtrasVersion exclude ("io.micrometer", "micrometer-core")

  //val typesafeConfig = "com.typesafe"         % "config" % versions.configTypesafeVersion
  val pureconfigCore = "com.github.pureconfig" %% "pureconfig-core" % versions.pureconfigVersion

  val okHttp = "com.squareup.okhttp3" % "okhttp" % versions.okHttpVersion

  val scalaTest    = "org.scalatest"              %% "scalatest"      % versions.scalaTestVersion
  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging"  % versions.scalaLoggingVersion
  val logback      = "ch.qos.logback"             % "logback-classic" % versions.logbackVersion
}
