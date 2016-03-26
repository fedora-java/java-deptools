package org.fedoraproject.javadeptools

import java.io.File
import java.io.IOException
import java.net.URL
import java.sql.Connection
import java.sql.SQLException
import java.util.jar.Manifest
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

import scala.collection.JavaConverters.mapAsScalaMapConverter
import scala.collection.mutable.ArrayBuffer

import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry
import org.fedoraproject.javadeptools.rpm.RpmArchiveInputStream

import anorm.BatchSql
import anorm.NamedParameter
import anorm.NamedParameter.symbol
import anorm.ParameterValue.toParameterValue
import anorm.ParameterValue.toParameterValue$default$2
import anorm.SqlStringInterpolation
import javax.sql.DataSource
import resource.managed

object DatabaseBuilder {
  private def findRpms(file: File): List[File] = {
    if (file.exists()) {
      if (file.isDirectory())
        file.listFiles.flatMap(findRpms).toList
      else List(file)
    } else Nil
  }

  private def processJar(is: ZipInputStream, fileId: Int, collectionId: Int)(implicit connection: Connection) = {
    var entry: ZipEntry = null
    val classParams = ArrayBuffer.empty[Seq[NamedParameter]]
    val manifestParams = ArrayBuffer.empty[Seq[NamedParameter]]
    while ({ entry = is.getNextEntry; entry != null }) {
      if (entry.getName().equals("META-INF/MANIFEST.MF")) {
        val manifest = new Manifest(is)
        for ((key, value) <- manifest.getMainAttributes.asScala) {
          manifestParams += Seq('key -> key.toString, 'value -> value.toString)
        }
      } else if (!entry.isDirectory() && entry.getName().endsWith(".class") && !entry.getName().contains("$")) {
        val nameParts = entry.getName().replaceFirst("\\.class$", "").split("/").toList;
        val namespace = if (nameParts.length - 1 > 0)
          nameParts.slice(0, nameParts.length - 1).mkString(".") else null
        val className = nameParts.last
        classParams += Seq('namespace -> namespace, 'class_name -> className)
      }
    }
    if (classParams.nonEmpty)
      BatchSql(s"""INSERT INTO class_entry(file_artifact_id, namespace, class_name, collection_id)
                 VALUES ($fileId, {namespace}, {class_name}, $collectionId)""", classParams).execute()
    if (manifestParams.nonEmpty)
      BatchSql(s"""INSERT INTO manifest_entry(file_artifact_id, key, value, collection_id)
                 VALUES ($fileId, {key}, {value}, $collectionId)""", manifestParams).execute()
  }

  private def processPackage(url: URL, collectionId: Int)(implicit connection: Connection) = {
    println(s"Processing $url")
    val path = new File(url.toURI())
    val name = path.getName.replaceFirst("\\.rpm$", "").replaceAll("-[^-]*-[^-]*$", "");
    val packageId = SQL"INSERT INTO package(name, collection_id) VALUES ($name, $collectionId)".executeInsert().get.toInt
    var is: RpmArchiveInputStream = null
    try {
      is = new RpmArchiveInputStream(path.toPath)
      var entry: ArchiveEntry = null
      while ({ entry = is.getNextEntry; entry != null }) {
        val cpioEntry = entry.asInstanceOf[CpioArchiveEntry]
        if (cpioEntry.isRegularFile && cpioEntry.getName.endsWith(".jar")) {
          val jarPath = entry.getName().replaceFirst("^\\.", "")
          val fileId = SQL"INSERT INTO file_artifact(path, package_id, collection_id) VALUES ($jarPath, $packageId, #$collectionId)"
            .executeInsert().get.toInt
          // JarInputStream is buggy
          processJar(new ZipInputStream(is), fileId, collectionId)
        }
      }
    } catch {
      case exception: IOException => {
        println(s"Skipping $path due to exception: $exception")
      }
    } finally {
      if (is != null)
        is.close()
    }
  }

  def buildFromPath(collectionName: String, path: File)(implicit ds: DataSource) {
    val rpms = findRpms(path)
    if (rpms.isEmpty) throw new RuntimeException(s"No RPMs found in $path")
    buildFromURLs(collectionName, Seq(path.toURL))
  }

  def buildFromURLs(collectionName: String, urls: Iterable[URL])(implicit ds: DataSource) {
    try {
      implicit val connection = ds.getConnection()
      try {
        SQL"DELETE FROM collection WHERE name = $collectionName AND NOT finalized".executeUpdate()
        val collectionId = SQL"INSERT INTO collection(name) VALUES ($collectionName)".executeInsert().get.toInt
        for {
          url <- urls.par
          threadConnection <- managed(ds.getConnection())
        } {
          threadConnection.setAutoCommit(false)
          processPackage(url, collectionId)(threadConnection)
          threadConnection.commit()
        }
        connection.setAutoCommit(false)
        SQL"DELETE FROM collection WHERE name = $collectionName AND finalized".executeUpdate()
        SQL"UPDATE collection SET finalized = true WHERE id = $collectionId".executeUpdate()
        connection.commit()
        connection.setAutoCommit(true)
        SQL"VACUUM FULL ANALYZE".execute()
      } finally {
        connection.close()
      }
    } catch {
      case exception: SQLException => {
        var e = exception
        do {
          e.printStackTrace()
        } while ({ e = e.getNextException; e != null })
        throw exception
      }
    }
  }
}