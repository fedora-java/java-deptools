import java.io.File

import scala.io.Source

import org.fedoraproject.javadeptools._
import org.fedoraproject.javadeptools.DatabaseBuilder
import org.scalatest.FlatSpec
import org.scalatest.Matchers

import com.zaxxer.hikari.HikariDataSource

import anorm.SQL
import anorm.SqlStringInterpolation
import anorm.sqlToSimple
import javax.sql.DataSource
import org.postgresql.Driver
import org.scalatest.BeforeAndAfterAll
import java.net.URI

class Test extends FlatSpec with Matchers with BeforeAndAfterAll {
  def createLocalTestDb = {
    {
      val ds = new HikariDataSource()
      ds.setDriverClassName("org.postgresql.Driver")
      ds.setJdbcUrl("jdbc:postgresql:postgres")
      ds.setUsername("postgres")
      implicit val conn = ds.getConnection()
      SQL"""DROP DATABASE IF EXISTS "java-deptools-test"""".execute()
      SQL"""CREATE DATABASE "java-deptools-test"""".execute()
      conn.close()
      ds.close()
    }
    {
      val ds = new HikariDataSource()
      ds.setDriverClassName("org.postgresql.Driver")
      ds.setJdbcUrl("jdbc:postgresql:java-deptools-test")
      ds.setUsername("java-deptools")
      implicit val conn = ds.getConnection
      conn.setAutoCommit(false)
      SQL(Source.fromInputStream(getClass.getResourceAsStream("schema.sql"), "UTF-8").mkString).execute()
      conn.commit()
      conn.close()
      ds
    }
  }

  implicit val hds = createLocalTestDb
  implicit val conn = hds.getConnection

  override def afterAll {
    conn.close()
    hds.close()
  }

  def compareClasses(name: String, fileId: Int) {

  }

  DatabaseBuilder.buildFromURLs("test1", Seq(
      "args4j-2.32-3.fc24.noarch.rpm",
      "sat4j-2.3.5-8.fc24.noarch.rpm"
      ).map(getClass.getResource(_)))
    DatabaseBuilder.buildFromURLs("test2", Seq(
      "sat4j-2.3.5-8.fc24.noarch.rpm"
      ).map(getClass.getResource(_)))

  it should "list all collections" in {
    val collections = DAO.findAllCollections
    collections(0).name shouldEqual "test1"
    collections(1).name shouldEqual "test2"
  }

  it should "find package by name" in {
    val pkg = DAO.findPackageByName(1, "args4j")
    pkg shouldBe defined
    pkg.get.name shouldEqual "args4j"
  }

  it should "find file by id" in {
    val file = DAO.findFileById(1)
    file shouldBe defined
  }

  it should "list jars in package" in {
    val pkg = DAO.findPackageByName(2, "sat4j").get
    val jars = DAO.findFilesForPackage(pkg.id)
    jars.map(_.path) should contain theSameElementsInOrderAs Seq(
        "/usr/share/java/org.sat4j.core.jar",
        "/usr/share/java/org.sat4j.pb.jar")
    jars.map(_.classCount) should contain theSameElementsInOrderAs Seq(
        216,
        129)
    jars.map(_.packageId) should contain only pkg.id
  }

//  it should "list all packages" in {
//
//  }

  it should "list the class tree" in {
    val expected = Source.fromInputStream(getClass.getResourceAsStream("args4j-classes.txt")).getLines().toArray
    val pkg = DAO.findPackageByName(1, "args4j").get
    val jars = DAO.findFilesForPackage(pkg.id)
    jars.size shouldEqual 1
    val classes = DAO.findClassesForFile(jars(0).id).map(_.qualifiedName)
    classes should contain theSameElementsAs expected
  }
}