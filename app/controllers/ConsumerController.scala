package controllers

package controllers

import models._
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Request
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._

object ConsumerController extends Controller {
  
  /** Quick, easy and unsecure way of creating a new consumer for the purposes of round trip */
  def postConsumer = Action { 
    Ok("Not Implemented")
  }

}