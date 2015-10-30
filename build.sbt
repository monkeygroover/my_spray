organization := "com.monkeygroover"
version := "0.1.0-SNAPSHOT"
scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-stream-experimental_2.11" % "1.0",
  "com.chuusai" %% "shapeless" % "2.2.5",
  "org.spire-math" %% "cats" % "0.2.0"
)
