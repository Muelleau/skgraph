ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val skbid = (project in file("."))
  .settings(
    name := "skgraph"
  )
