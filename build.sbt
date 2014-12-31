name := "nyandoc"

organization := "com.todesking"

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  "org.jsoup"              %  "jsoup"                     % "1.7.3"
)

scalacOptions ++= Seq("-deprecation", "-feature")

scalacOptions in (Compile, doc) ++= Seq("-groups", "-implicits")

seq(conscriptSettings :_*)

resourceGenerators in Compile <+=
  (sourceManaged in Compile, organization, name, version, scalaVersion) map { case (out, org, name, ver, scalaVer) =>
    val content = """[app]
    |  version: ${version}
    |  org: ${org}
    |  name: ${name}
    |  class: ${className}
    |[scala]
    |  version: ${scalaVersion}
    |[repositories]
    |  local
    |  scala-tools-releases
    |  maven-central
    """.stripMargin
    import java.io._
    val dir = new File(out, "conscript/nyandoc")
    dir.mkdirs()
    val launchcondig = new File(dir, "launchcondig")
    val os = new FileOutputStream(launchcondig)
    try {
      val writer = new BufferedWriter(new OutputStreamWriter(os))
      writer.write(content)
      writer.close()
    } finally {
      os.close()
    }
    Seq(launchcondig)
  }
