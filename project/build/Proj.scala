import sbt._

class Proj(info: ProjectInfo) extends DefaultProject(info) {

    val scalatest = "org.scalatest" % "scalatest" % "1.3" % "test"

}
