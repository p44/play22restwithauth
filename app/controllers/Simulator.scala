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
  
  /**
   * Simulated callback
   * PUT /simulator/mycallback
   */
  def putMyCallback = Action { request =>
    val jsonBody: Option[JsValue] = request.body.asJson
    play.api.Logger.debug("putMyCallback " + request.body)
    jsonBody match {
      case None => { BadRequest("Could not resolve request body as json.  Do you have the 'Content-type' header set to 'application/json'? " + request.body) }
      case _ => { 
        play.api.Logger.info("putMyCallback success: " + jsonBody.get)
        Ok("Ack") 
      }
    }
  }

}