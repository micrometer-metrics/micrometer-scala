name := "micrometer-scala-root"

scalafmtOnCompile in ThisBuild := true

lazy val micrometerScala = (project in file("micrometer-scala"))
  .settings(
    Seq(
      name := "micrometer-scala",
      libraryDependencies ++= Seq(
        CommonDeps.scalaCollectionCompat,
        CommonDeps.scalaJava8Compat,
        CommonDeps.micrometerCore       % Provided,
        CommonDeps.micrometerOpenTSDB   % Provided,
        CommonDeps.micrometerPrometheus % Provided,
        CommonDeps.micrometerJvmExtras  % Provided,
        CommonDeps.pureconfigCore,
        CommonDeps.scalaLogging % Test,
        CommonDeps.logback      % Test,
        CommonDeps.scalaTest    % Test
      )
    )
  )
  .settings(CommonSettings.defaultProjectSettings)

lazy val demoApp = (project in file("demo-app"))
  .dependsOn(micrometerScala)
  .settings(
    Seq(
      //fork in run := true,
      libraryDependencies ++= Seq(
        CommonDeps.logback,
        CommonDeps.scalaLogging,
        CommonDeps.micrometerCore,
        CommonDeps.micrometerJvmExtras,
        CommonDeps.micrometerOpenTSDB,
        CommonDeps.micrometerPrometheus,
        CommonDeps.okHttp,
        CommonDeps.scalaTest % Test //,
        //"io.dropwizard.metrics" % "metrics-core" % "4.0.6" % Test
      )
    )
  )
  .settings(CommonSettings.defaultProjectSettings)
  .settings(publish := {}, publishLocal := {})

lazy val root =
  (project in file("."))
    .aggregate(micrometerScala, demoApp)
    .settings(CommonSettings.defaultProjectSettings)
    .settings(publish := {}, publishLocal := {})
