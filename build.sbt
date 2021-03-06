name := """nexproto-t7-penalty"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs
)

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.3.0-2",
  "org.webjars" % "bootstrap" % "3.1.1-2",
  "org.webjars" % "jquery" % "2.1.3",
  "org.webjars" % "jquery-ui" % "1.11.4",
  "mysql" % "mysql-connector-java" % "5.1.35",
  "commons-codec" % "commons-codec" % "1.10",
  "org.apache.httpcomponents" % "httpclient" % "4.4.1",
  "org.ocpsoft.prettytime" % "prettytime" % "4.0.0.Final"
)
