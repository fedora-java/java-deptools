import java.io.File

import scopt._
import scalikejdbc.ConnectionPool

import org.fedoraproject.javadeptools.DatabaseBuilder._

case class Config(
  dbUrl: String = "jdbc:postgresql:java-deptools",
  dbUser: String = "java-deptools",
  command: Symbol = 'none,
  collection: String = "default",
  file: File = null)

object Cli {
  val parser = new OptionParser[Config]("java-deptools") {
    opt[String]('d', "database") action { (v, c) => c.copy(dbUrl = v) } text "Database URL"
    opt[String]('u', "user") action { (v, c) => c.copy(dbUser = v) } text "Database user"
    cmd("build") action { (v, c) => c.copy(command = 'build) } children {
      opt[String]('c', "collection") action { (v, c) => c.copy(collection = v) }
      arg[File]("file") action { (v, c) => c.copy(file = v) }
    }
  }

  def main(args: Array[String]) {
    parser.parse(args, Config()) match {
      case Some(config) => {
        ConnectionPool.singleton(config.dbUrl, config.dbUser, "")
        implicit val pool = ConnectionPool()
        config.command match {
          case 'build => buildFromPath(config.collection, config.file)
          case _ => System.exit(1)
        }
      }
      case _ => System.exit(1)
    }
  }
}