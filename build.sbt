organization := "com.todesking"

name := "nyandoc"

version := "0.0.1"

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  "org.jsoup"               %  "jsoup"         % "1.7.3",
  "org.json4s"              %% "json4s-native" % "3.2.10"
)

scalacOptions ++= Seq("-deprecation", "-feature")

scalacOptions in (Compile, doc) ++= Seq("-groups", "-implicits")

seq(conscriptSettings :_*)
