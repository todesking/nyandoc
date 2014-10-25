name := "dox"

organization := "com.todesking"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "org.jsoup"              %  "jsoup"                     % "1.7.3"
)

scalacOptions ++= Seq("-deprecation", "-feature")
