import sbt._
import sbt.Keys._

object ApplicationBuild extends Build {
  val generateConscript = Def.task {
    val content = s"""[app]
      |  version: ${version.value.replaceAll("\\+$", "")}
      |  org: ${organization.value}
      |  name: ${name.value}
      |  class: com.todesking.nyandoc.Main
      |[scala]
      |  version: ${scalaVersion.value}
      |[repositories]
      |  local
      |  scala-tools-releases
      |  maven-central
      |  todesking: http://todesking.github.io/mvn/""".stripMargin
    val dir = (sourceDirectory in Compile).value / "conscript" / "nyandoc"
    dir.mkdirs()
    val launchconfig = dir / "launchconfig"
    IO.write(launchconfig, content)
  }
}
