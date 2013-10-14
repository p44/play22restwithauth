package controllers

import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Request
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._

/**
 * Simulates a Consumer of the registration service
 */
object Simulator extends Controller {
  
  def index = Action {
    Ok(views.html.simulator.render)
  }

}