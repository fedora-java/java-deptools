package controllers

import collection.JavaConverters._
import java.io.File
import play.api.mvc.{ Controller, Action }
import org.fedoraproject.javadeptools.impl.DatabaseFactory
import org.fedoraproject.javadeptools.Database

object Application extends Controller {

  val dbVal = new ThreadLocal[Database];

  def db = {
    if (dbVal.get == null) {
      dbVal.set(new DatabaseFactory().createDatabase(new File("/home/msimacek/git/java-deptools/db")))
    }
    dbVal.get
  }

  def index = Action { implicit request =>
    val content = request.getQueryString("q").map { q => db.queryClasses("%" + q + "%").asScala }
    Ok(views.html.index(content))
  }

  //  def results = Action { implicit request =>
  ////    val query = SQL("""
  ////      SELECT class_entry.name, file.path
  ////      FROM class_entry JOIN class_file_relation ON class_entry.id = class_id
  ////           JOIN file ON file_id = file.id
  ////      """)
  ////    val parser = for {
  ////      c <- str("class_entry.name")
  ////      f <- str("file.path")
  ////    } yield Entry(c, f)
  ////    DB.withConnection { implicit c =>
  ////      val results = query.as(parser *)
  ////      Ok(views.html.index(Some(results)))
  ////    }
  //    match request.getQueryString("q")) {
  //      case Some(q): Ok(views.html.indexdb.queryClasses()
  //  } 

}