package controllers

import java.sql.Connection

import scala.language.implicitConversions

import org.fedoraproject.javadeptools.ClassResultJoined
import org.fedoraproject.javadeptools.DAO.findAllCollections
import org.fedoraproject.javadeptools.DAO.findClassesForFile
import org.fedoraproject.javadeptools.DAO.findCollectionByName
import org.fedoraproject.javadeptools.DAO.findFileById
import org.fedoraproject.javadeptools.DAO.findFilesForPackage
import org.fedoraproject.javadeptools.DAO.findManifestEntriesForFile
import org.fedoraproject.javadeptools.DAO.findPackageByName
import org.fedoraproject.javadeptools.DAO.queryClasses
import org.fedoraproject.javadeptools.DAO.queryManifests
import org.fedoraproject.javadeptools.ManifestResultJoined
import org.fedoraproject.javadeptools.Page

import javax.inject.Inject
import play.api.data.Form
import play.api.data.Forms.boolean
import play.api.data.Forms.default
import play.api.data.Forms.mapping
import play.api.data.Forms.text
import play.api.db.Database
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Request
import play.api.mvc.Result
import views.html.helper.FieldConstructor

object implicits {
  implicit val fc = FieldConstructor(views.html.field_constructor.f)
}

abstract class SearchResults
case class ClassResults(result: Page[ClassResultJoined]) extends SearchResults
case class ManifestResults(result: Page[ManifestResultJoined]) extends SearchResults

case class SearchData(queryType: String, query: String, query2: String,
  collectionName: String, caseSensitive: Boolean)

class Application @Inject() (db: Database)(val messagesApi: MessagesApi) extends Controller with I18nSupport {
  implicit def optionToResult(option: Option[Result]) = option match {
    case Some(x) => x
    case None => NotFound
  }

  object DBAction {
    def apply(block: Request[AnyContent] => Connection => Result) = Action { request =>
      db.withConnection { conn => block(request)(conn) }
    }
  }

  val searchForm = Form(
    mapping(
      "qtype" -> default(text, "classes"),
      "q" -> default(text, ""),
      "q2" -> default(text, ""),
      "collection" -> default(text, ""),
      "cs" -> default(boolean, false))(SearchData.apply)(SearchData.unapply))

  def index(pageNo: Int) = DBAction { implicit request =>
    implicit connection =>
      val form = searchForm.bindFromRequest
      implicit val formData = form.get
      val collections = findAllCollections
      if (collections.isEmpty) {
        InternalServerError("Database not populated yet. No collections setup.")
      } else {
        val collection = collections.find(_.name == formData.collectionName).getOrElse(collections.head)
        val content = if (formData.query.length > 0) {
          formData.queryType match {
            case "classes" =>
              val page = queryClasses(collection.id, formData.query.replace('/', '.') + '%', formData.caseSensitive, pageNo)
              Some(ClassResults(page))
            case "manifests" =>
              val page = queryManifests(collection.id, formData.query, '%' + formData.query2 + '%',
                formData.caseSensitive, pageNo)
              Some(ManifestResults(page))
            case _ => None
          }
        } else None
        Ok(views.html.index(form, collections, collection, content))
      }
  }

  def about = Action(implicit request => Ok(views.html.about()))

  def packageDetail(collectionName: String, name: String) = DBAction { implicit request =>
    implicit connection =>
      for {
        collection <- findCollectionByName(collectionName)
        pkg <- findPackageByName(collection.id, name)
        files = findFilesForPackage(pkg.id)
      } yield Ok(views.html.package_detail(pkg, files))
  }

  def fileArtifactDetail(fileId: Int) = DBAction { implicit request =>
    implicit connection =>
      for {
        file <- findFileById(fileId)
        classes = findClassesForFile(fileId)
        manifestEntries = findManifestEntriesForFile(fileId)
      } yield Ok(views.html.file_detail(file, classes, manifestEntries))
  }
}