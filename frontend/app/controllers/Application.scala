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

  val dbVal = new ThreadLocal[Database];

  def db = {
    if (dbVal.get == null) {
      dbVal.set(new DatabaseFactory().createDatabase("jdbc:h2:tcp://localhost/~/test"))
    }
    dbVal.get
  }

  def index = Action { implicit request =>
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
}