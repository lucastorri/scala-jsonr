organization := "co.torri"

name := "scala-jsonr"

version := "0.5"

scalaVersion := "2.9.1"

scalacOptions += "-deprecation"

scalacOptions += "-unchecked"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "1.6.1" % "test"
)

homepage := Some(url("https://github.com/lucastorri/scala-jsonr/"))

publishMavenStyle := true

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT")) 
    Some("snapshots" at nexus + "content/repositories/snapshots") 
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

seq(assemblySettings: _*)

pomExtra := (
  <licenses>
    <license>
      <name>MIT license</name>
      <url>http://www.opensource.org/licenses/mit-license.php</url>
    </license>
  </licenses>
  <scm>
    <url>git://github.com/lucastorri/scala-jsonr.git</url>
    <connection>scm:git:git://github.com/lucastorri/scala-jsonr.git</connection>
  </scm>
  <developers>
    <developer>
      <id>lucastorri</id>
      <name>Lucas Torri</name>
      <url>http://latestbuild.net/</url>
    </developer>
  </developers>
)
