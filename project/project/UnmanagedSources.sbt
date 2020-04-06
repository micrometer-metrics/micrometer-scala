unmanagedSources in Compile ++= {
  val rootProjDir = baseDirectory.value.getParentFile
  Seq(
    rootProjDir / "CommonSettings.scala",
    rootProjDir / "CommonDeps.scala"
  )
}
