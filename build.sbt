import sbt._
import Keys._
import sbtassembly.MergeStrategy

name := "email"

version := "0.0.1"

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.3",
  "org.scala-lang" % "scala-compiler" % "2.11.12",
  "org.json4s" %% "json4s-core" % "3.5.3",
  "javax.mail" % "mail" % "1.4.7"
)

val defaultMergeStrategy: String => MergeStrategy = {
  case m if m.toLowerCase.endsWith("manifest.mf") => MergeStrategy.discard
  case m if m.startsWith("META-INF") => MergeStrategy.discard
  case PathList("javax", "servlet", xs@_*) => MergeStrategy.last
  case PathList("org", "apache", xs@_*) => MergeStrategy.last
  case PathList("org", "jboss", xs@_*) => MergeStrategy.last
  case "log4j.properties" => MergeStrategy.discard
  case "about.html" => MergeStrategy.rename
  case "reference.conf" => MergeStrategy.concat
  case _ => MergeStrategy.last
}

scalacOptions += "-deprecation"
mainClass in assembly := Some("com.spark.mail.Email")
test in assembly := {}
assemblyMergeStrategy in assembly := defaultMergeStrategy
assemblyJarName in assembly :="Scala_Spark_Mail.jar"