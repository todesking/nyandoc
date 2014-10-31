name := "example"

scalaVersion := "2.11.4"

scalacOptions in (Compile, doc) ++= Seq("-groups", "-implicits", "-implicits-show-all")
