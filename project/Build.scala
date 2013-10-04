import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "Play22RestWithAuth"
  val appVersion      = "0.0.1"

  playScalaSettings

  val appDependencies = Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.2.0",
    "com.typesafe.akka" %% "akka-slf4j" % "2.2.0",
    "org.webjars" % "webjars-play_2.10" % "2.2.0",
    "com.typesafe.akka" %% "akka-testkit" % "2.2.0" % "test"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    scalaVersion := "2.10.2"
  )

}
