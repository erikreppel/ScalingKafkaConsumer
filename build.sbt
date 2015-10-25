name := "ScalingConsumer"

version := "0.2.1"

scalaVersion := "2.11.7"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

libraryDependencies ++= Seq(
  "org.apache.kafka" %% "kafka" % "0.8.2.1",
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  "com.101tec" % "zkclient" % "0.2",
  "com.typesafe.akka" %% "akka-actor" % "2.4-SNAPSHOT",
  "log4j" % "log4j" % "1.2.17",
  "org.scalaj" %% "scalaj-http" % "1.1.5"
)
