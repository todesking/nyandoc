name := "nyandoc"

organization := "com.todesking"

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  "org.jsoup"              %  "jsoup"                     % "1.7.3"
)

scalacOptions ++= Seq("-deprecation", "-feature")

scalacOptions in (Compile, doc) ++= Seq("-groups", "-implicits")

seq(conscriptSettings :_*)
