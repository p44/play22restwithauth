package controllers

import play.api.mvc.Action
import play.api.mvc.Controller
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._

object Register extends Controller {

  /** GET / */
  def index = Action.async {
    val hello: Future[String] = Future { "Hello" }
    hello.map(s => Ok(s))
  }

}