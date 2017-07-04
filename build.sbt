name := "test-task"
organization := "com.dminyaev"
version := "1.0"
scalaVersion := "2.11.8"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

lazy val root = (project in file(".")).enablePlugins(JavaAppPackaging, DockerPlugin)

packageName in Docker := "test-task-docker"
dockerExposedPorts := Seq(9000)

libraryDependencies ++= {
  val akkaVersion       = "2.5.2"
  val akkaHttpVersion   = "10.0.5"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
    "org.scalatest"     %% "scalatest" % "3.0.1" % "test",
    "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided",
    "org.scalaz" % "scalaz-core_2.11" % "7.2.13"
  )
}
unmanagedResourceDirectories in Compile += {
  baseDirectory.value / "src/main/resources"
}

Revolver.settings


