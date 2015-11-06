package controllers

import collection.JavaConverters._
import java.io.File
import play.api.mvc.{ Controller, Action }
import org.fedoraproject.javadeptools.Query
import play.api.mvc.Request
import play.api.Play
import play.api.data.Form
import play.api.data.Forms.{ mapping, text, default }
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import com.google.inject.Guice
import org.fedoraproject.javadeptools.impl.JavaDeptoolsModule
import org.fedoraproject.javadeptools.model.{ ClassEntry, ManifestEntry }
import org.fedoraproject.javadeptools.data.ClassEntryDao
import org.fedoraproject.javadeptools.data.FileArtifactDao
import org.fedoraproject.javadeptools.data.PackageDao
import org.fedoraproject.javadeptools.data.PackageCollectionDao
import org.fedoraproject.javadeptools.data.ManifestEntryDao
import scala.collection.immutable.HashMap
import com.google.inject.persist.jpa.JpaPersistModule
import scala.collection.Searching.SearchResult
import views.html.helper.FieldConstructor

object Page {
  val itemsPerPage = 100
  def create[T](query: Query[T], currentPage: Int)(implicit request: Request[Any]) = {
    val total = query.getTotal
    val pages = total / itemsPerPage + 1
    if (currentPage < 1 || currentPage > pages) {
      None
    } else {
      Some(new Page(query.getResults((currentPage - 1) * itemsPerPage, itemsPerPage).asScala, currentPage, total))
    }
  }
}

object implicits {
  implicit val fc = FieldConstructor(views.html.field_constructor.f)
}

case class Page[T](content: Iterable[T], currentPage: Int, totalCount: Long) {
  val from = (currentPage - 1) * Page.itemsPerPage
  val to = from + content.size
  val maxPage = totalCount / Page.itemsPerPage
}

abstract class SearchResults
case class ClassResults (result: Page[ClassEntry]) extends SearchResults
case class ManifestResults (result: Page[ManifestEntry]) extends SearchResults

case class SearchData(queryType: String, query: String, query2: String, collectionName: String)

object Application extends Controller {

  val dbProps = HashMap("javax.persistence.jdbc.url" ->
    Play.current.configuration.getString("db.default.url").get,
    "javax.persistence.jdbc.driver" ->
      Play.current.configuration.getString("db.default.driver").get,
    "javax.persistence.jdbc.user" ->
      Play.current.configuration.getString("db.default.user").get,
    "javax.persistence.jdbc.password" ->
      Play.current.configuration.getString("db.default.password").get)
  lazy val injector = JavaDeptoolsModule.createInjector(dbProps.asJava)
  lazy val collectionDao = injector.getInstance(classOf[PackageCollectionDao])
  lazy val classDao = injector.getInstance(classOf[ClassEntryDao])
  lazy val fileDao = injector.getInstance(classOf[FileArtifactDao])
  lazy val packageDao = injector.getInstance(classOf[PackageDao])
  lazy val manifestDao = injector.getInstance(classOf[ManifestEntryDao])

  val searchForm = Form(
    mapping(
      "qtype" -> default(text, "classes"),
      "q" -> default(text, ""),
      "q2" -> default(text, ""),
      "collection" -> default(text, ""))(SearchData.apply)(SearchData.unapply))

  def index(page: Int, q: String, collectionName: Option[String]) = Action { implicit request =>
    val form = searchForm.bindFromRequest
    val formData = form.get
    val collections = collectionDao.getAllCollections.asScala;
    val collection = collections.find(_.getName() == formData.collectionName).getOrElse(collections.head)
    val content = formData match {
      case SearchData(_, "", _, _) => None
      case SearchData("classes", q: String, _, _) =>
        val query = classDao.queryClassEntriesByName(collection, q + '%')
        Page.create(query, page).map(ClassResults(_))
      case SearchData("manifests", q: String, q2: String, _) =>
        val query = manifestDao.queryByManifest(collection, q, '%' + q2 + '%')
        Page.create(query, page).map(ManifestResults(_))
      case _ => None
    }
    Ok(views.html.index(form, collections, collection, content))
  }

  def about = Action(implicit request => Ok(views.html.about()))

  def packageDetail(collectionName: String, name: String) = Action { implicit request =>
    val pkg = for {
      collection <- Option(collectionDao.getCollectionByName(collectionName))
      pkg <- Option(packageDao.getPackageByName(collection, name))
    } yield pkg
    pkg match {
      case None => NotFound
      case Some(pkg) => Ok(views.html.package_detail(pkg))
    }
  }

  def fileArtifactDetail(pkgName: String, fileId: Long) = Action { implicit request =>
    val file = fileDao.findById(fileId);
    Ok(views.html.file_detail(file))
  }
}