organization := "com.todesking"

name := "nyandoc"

version := "0.0.2-SNAPSHOT"

scalaVersion := "2.11.4"

publishTo := Some(Resolver.file("com.todesking",file("./repo/"))(Patterns(true, Resolver.mavenStyleBasePattern)))

libraryDependencies ++= Seq(
  "org.jsoup"               %  "jsoup"         % "1.7.3",
  "org.json4s"              %% "json4s-native" % "3.2.10"
)

scalacOptions ++= Seq("-deprecation", "-feature")

sourceGenerators in Compile <+= (sourceManaged in Compile, version) map { (dir, v) =>
  val file = dir / "Version.scala"
  IO.write(file, s"""package com.todesking.nyandoc
    |object Version {
    |  val string = "${v}"
    |}""".stripMargin)
  Seq(file)
}

compile <<= (compile in Compile) dependsOn generateConscript

seq(conscriptSettings :_*)
