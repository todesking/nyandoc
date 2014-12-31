import sbt._
import sbt.Keys._

object ApplicationBuild extends Build {
  lazy val root = Project("nyandoc", file("."))
    .settings(
      TaskKey[Unit]("generateConscript") := {
        val content = s"""[app]
        |  version: ${version.value}
        |  org: ${organization.value}
        |  name: ${name.value}
        |  class: com.todesking.nyandoc.Main
        |[scala]
        |  version: ${scalaVersion.value}
        |[repositories]
        |  local
        |  scala-tools-releases
        |  maven-central
        """.stripMargin
        val dir = (sourceDirectory in Compile).value / "conscript" / "nyandoc"
        dir.mkdirs()
        val launchcondig = dir / "launchcondig"
        IO.write(launchcondig, content)
      }
    )
}
