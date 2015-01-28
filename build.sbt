organization := "com.todesking"

name := "nyandoc"

version := "0.0.2"

scalaVersion := "2.11.4"

publishTo := Some(Resolver.file("com.todesking",file("./repo/"))(Patterns(true, Resolver.mavenStyleBasePattern)))

libraryDependencies ++= Seq(
  "org.jsoup"               %  "jsoup"         % "1.7.3",
  "org.json4s"              %% "json4s-native" % "3.2.10"
)

scalacOptions ++= Seq("-deprecation", "-feature")

scalacOptions in (Compile, doc) ++= Seq("-groups", "-implicits")

seq(conscriptSettings :_*)
