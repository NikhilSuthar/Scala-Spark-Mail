name := "email"

version := "0.0.1"

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.3",
  "org.scala-lang" % "scala-compiler" % "2.11.12",
  "org.json4s" %% "json4s-core" % "3.5.3",
  "javax.mail" % "mail" % "1.4.7"
)
