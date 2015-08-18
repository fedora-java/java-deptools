package controllers

import collection.JavaConverters._
import java.io.File
import play.api.mvc.{ Controller, Action }
import org.fedoraproject.javadeptools.impl.DatabaseFactory
import org.fedoraproject.javadeptools.Database
import org.fedoraproject.javadeptools.Query
import play.api.mvc.Request
import play.api.Play

object Page {
  val itemsPerPage = 100
  def create[T](query: Query[T], currentPage: Int)(implicit request: Request[Any]) = {
    val pages = query.getCount / itemsPerPage
    val total = query.getCount
    if (currentPage < 1 || currentPage > pages) {
      None
    } else {
      query.setLimits((currentPage - 1) * itemsPerPage, itemsPerPage)
      Some(new Page(query.getResults.asScala, currentPage, total))
    }
  }
}

case class Page[T](content: Iterable[T], currentPage: Int, totalCount: Int) {
  val from = (currentPage - 1) * Page.itemsPerPage
  val to = from + content.size
  val maxPage = totalCount / Page.itemsPerPage
}

object Application extends Controller {

  lazy val dbFactory = new DatabaseFactory(Play.current.configuration.getString("java-deptools.db.url").get)

  def index(page: Int, q: String) = Action { implicit request =>
    val db = dbFactory.createDatabase()
    if (q == "") {
      Ok(views.html.index(None))
    } else {
      val query = db.queryClasses("%" + q + "%")
      val content = Page.create(query, page)
      Ok(views.html.index(content))
    }

  }

  def about = Action(implicit request => Ok(views.html.about()))

  def packageDetail(name: String) = Action { implicit request =>
    val db = dbFactory.createDatabase()
    val pkg = db.getPackage(name)
    if (pkg == null) NotFound else Ok(views.html.package_detail(pkg))
  }
}