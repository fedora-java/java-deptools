package org.fedoraproject.javadeptools

import anorm._
import java.sql.Connection
import anorm.ParameterValue.toParameterValue

trait PageTrait {
  def currentPage: Int
  def totalCount: Int
  def from: Int
  def to: Int
  def maxPage: Int
}

case class Page[T](query: PaginatedQuery[T], currentPage: Int, itemsPerPage: Int = 100) extends PageTrait {
  val from = (currentPage - 1) * itemsPerPage
  lazy val items = query.items(itemsPerPage, from)
  lazy val to = from + items.size
  lazy val totalCount = query.totalCount
  lazy val maxPage = totalCount / itemsPerPage
}

trait PaginatedQuery[T] {
  def items(limit: Int, offset: Int): List[T]
  def totalCount: Int
}

case class CollectionResult(
  id: Int,
  name: String)

case class PackageResult(
  id: Int,
  name: String,
  collectionId: Int)

case class FileResult(
  id: Int,
  packageId: Int,
  path: String)

case class FileResultWithCount(
  id: Int,
  packageId: Int,
  path: String,
  classCount: Int)

case class ClassResult(
  id: Int,
  namespace: Option[String],
  className: String,
  fileId: Int)

case class ClassResultJoined(
  id: Int,
  namespace: Option[String],
  className: String,
  fileId: Int,
  filePath: String,
  packageId: Int,
  packageName: String)

case class ManifestResult(
  id: Int,
  key: String,
  value: String,
  fileId: Int)

case class ManifestResultJoined(
  id: Int,
  key: String,
  value: String,
  fileId: Int,
  filePath: String,
  packageId: Int,
  packageName: String)

object RowParsers {
  val classParser = Macro.namedParser[ClassResult]
  val classJoinedParser = Macro.namedParser[ClassResultJoined]
  val packageParser = Macro.namedParser[PackageResult]
  val fileParser = Macro.namedParser[FileResult]
  val fileWithCountParser = Macro.namedParser[FileResultWithCount]
  val collectionParser = Macro.namedParser[CollectionResult]
  val manifestParser = Macro.namedParser[ManifestResult]
  val manifestJoinedParser = Macro.namedParser[ManifestResultJoined]
}

object DAO {
  def findCollectionByName(name: String)(implicit connection: Connection) =
    SQL"SELECT id, name FROM collection WHERE finalized AND name = $name".as(RowParsers.collectionParser.singleOpt)

  def findAllCollections(implicit connection: Connection) =
    SQL"SELECT id, name FROM collection WHERE finalized".as(RowParsers.collectionParser.*)

  def findPackageByName(collectionId: Int, name: String)(implicit connection: Connection) =
    SQL"""SELECT id, name, collection_id AS collectionId
          FROM package WHERE collection_id = $collectionId AND name = $name
    """.as(RowParsers.packageParser.singleOpt)

  def findFilesForPackage(packageId: Int)(implicit connection: Connection) = {
    // XXX count
    SQL""" SELECT id, package_id as packageId, path, 0 AS classCount
           FROM file_artifact WHERE package_id = $packageId
           ORDER BY path
    """.as(RowParsers.fileWithCountParser.*)
  }

  def findFileById(fileId: Int)(implicit connection: Connection) =
    SQL"SELECT id, package_id AS packageId, path FROM file_artifact WHERE id = $fileId".as(RowParsers.fileParser.singleOpt)

  def findClassesForFile(fileId: Int)(implicit connection: Connection) =
    SQL"""SELECT id, namespace, class_name AS className, file_artifact_id AS fileId
          FROM class_entry WHERE file_artifact_id = $fileId
          ORDER BY class_name, namespace
    """.as(RowParsers.classParser.*)

  def findManifestEntriesForFile(fileId: Int)(implicit connection: Connection) =
    SQL"""SELECT id, key, value, file_artifact_id AS fileId
          FROM manifest_entry WHERE file_artifact_id = $fileId
          ORDER BY key
    """.as(RowParsers.manifestParser.*)

  def queryClasses(collectionId: Int, nameQuery: String,
    caseSensitive: Boolean, currentPage: Int)(implicit connection: Connection) =
    new Page(new ClassEntryQuery(collectionId, nameQuery, caseSensitive), currentPage)

  def queryManifests(collectionId: Int, keyQuery: String, valueQuery: String,
    caseSensitive: Boolean, currentPage: Int)(implicit connection: Connection) =
    new Page(new ManifestEntryQuery(collectionId, keyQuery, valueQuery, caseSensitive), currentPage)
}

private class ClassEntryQuery(collectionId: Int, nameQuery: String,
  caseSensitive: Boolean)(implicit connection: Connection)
    extends PaginatedQuery[ClassResultJoined] {
  def items(limit: Int, offset: Int) = SQL"""
    WITH entries AS (
      SELECT * FROM class_entry
        WHERE collection_id = $collectionId
              AND (lower(class_name) LIKE lower($nameQuery)
                   OR lower(namespace||'.'||class_name) LIKE lower($nameQuery))
              AND (NOT $caseSensitive OR class_name like $nameQuery
                   OR namespace||'.'||class_name like $nameQuery)
    ),
    entries_sorted AS (
      SELECT * FROM entries ORDER BY class_name, namespace LIMIT $limit OFFSET $offset
    )
    SELECT entries_sorted.id AS id, namespace, class_name as className,
           file_artifact.id AS fileId, file_artifact.path AS filePath,
           package.id AS packageId, package.name AS packageName
      FROM package JOIN file_artifact ON package.id = package_id
                   JOIN entries_sorted ON file_artifact.id = entries_sorted.file_artifact_id
      WHERE package.collection_id = $collectionId AND file_artifact.collection_id = $collectionId
    """.as(RowParsers.classJoinedParser.*)

  def totalCount = SQL"""
      SELECT count(*) FROM class_entry
        WHERE collection_id = $collectionId
              AND (lower(class_name) LIKE lower($nameQuery)
                   OR lower((namespace||'.'||class_name)) LIKE lower($nameQuery))
              AND (class_name like $nameQuery
                   OR (namespace||'.'||class_name) like $nameQuery)
    """.as(SqlParser.scalar[Int].single)
}

private class ManifestEntryQuery(collectionId: Int, keyQuery: String, valueQuery: String,
    caseSensitive: Boolean)(implicit connection: Connection) extends PaginatedQuery[ManifestResultJoined] {
  val like = if (caseSensitive) "LIKE" else "ILIKE"
  def items(limit: Int, offset: Int) = SQL"""
    WITH entries AS (
      SELECT id, key, value, file_artifact_id FROM manifest_entry
      WHERE collection_id = $collectionId AND key #$like $keyQuery AND value #$like $valueQuery
      ORDER BY key LIMIT $limit OFFSET $offset
    )
    SELECT entries.id AS id, key, value, file_artifact_id AS fileId, path AS filePath,
           package_id AS packageId, package.name AS packageName
      FROM package JOIN file_artifact ON package.id = file_artifact.package_id
                   JOIN entries ON file_artifact.id = entries.file_artifact_id
      WHERE package.collection_id = $collectionId AND file_artifact.collection_id = $collectionId
    """.as(RowParsers.manifestJoinedParser.*)

  def totalCount = SQL"""
      SELECT count(*) FROM manifest_entry
      WHERE collection_id = $collectionId AND key LIKE $keyQuery AND value LIKE $valueQuery
    """.as(SqlParser.scalar[Int].single)
}