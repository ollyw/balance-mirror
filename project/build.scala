import sbt._
 import Keys._
 
object BalanceMirrorBuild extends Build {
  def scalaSettings = Seq(
    scalaVersion := "2.10.2",
    scalacOptions ++= Seq(
      "-optimize",
      "-unchecked",
      "-deprecation"
    )
  )

  def buildSettings =
    Project.defaultSettings ++
    scalaSettings ++
    (libraryDependencies += "org.scala-lang" % "scala-swing" % "2.10+")

  lazy val root = {
    val settings = buildSettings ++ Seq(name := "BalanceMirror")
    Project(id = "BalanceMirror", base = file("."), settings = settings)
  }
}
