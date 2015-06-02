package controllers

import collection.JavaConverters._
import java.io.File
import play.api.mvc.{ Controller, Action }
import org.fedoraproject.javadeptools.impl.DatabaseFactory
import org.fedoraproject.javadeptools.Database
import org.fedoraproject.javadeptools.Query
import play.api.mvc.Request

object Page {
  val itemsPerPage = 100
  def apply[T](query: Query[T])(implicit request: Request[Any]) = {
    val pages = query.getCount / itemsPerPage
    val currentPage = request.getQueryString("page").map(_.toInt).getOrElse(1)
    val total = query.getCount
    query.setLimits((currentPage - 1) * itemsPerPage, itemsPerPage)
    new Page(query.getResults.asScala, currentPage, total)
  }
}

case class Page[T](content: Iterable[T], currentPage: Int, totalCount: Int) {
  val from = (currentPage - 1) * Page.itemsPerPage
  val to = from + content.size
}

object Application extends Controller {

  lazy val dbFactory = new DatabaseFactory("jdbc:h2:tcp://localhost/~/test")

  def index = Action { implicit request =>
    val db = dbFactory.createDatabase()
    try {
      val content = request.getQueryString("q").map { q =>
        val query = db.queryClasses("%" + q + "%")
        Page(query)
      }
      Ok(views.html.index(content))
    } catch {
      case _: NumberFormatException => BadRequest
    }
  }

  def about = Action(implicit request => Ok(views.html.about()))

  def packageDetail(name: String) = Action { implicit request =>
    val db = dbFactory.createDatabase()
    val pkg = db.getPackage(name)
    if (pkg == null) NotFound else Ok(views.html.package_detail(pkg))
  }
}