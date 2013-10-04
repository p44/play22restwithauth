package controllers

import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Request
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._

object Register extends Controller {

  /** GET / */
  def index = Action.async {
    val f: Future[String] = Future { "Hello" }
    f.map(s => Ok(s))
  }

  /**
   * GET the customer's callback configuration.
   *  id is the customer id.
   */
  def getCallbackregistration(id: Long) = Action.async { request =>
    val f: Future[String] = Future { """{"id":1, "foo":"true"}""" }
    f.map(s => Ok(s))
  }

  /**
   * PUT the customer's callback configuration.
   *  id is the customer id.
   */
  def putCallbackregistration(id: Long) = Action.async { request =>
    val f: Future[(Boolean, String)] = Future { updateCallbackregistration(id, request) }
    f.map { bs =>
      {
        bs._1 match {
          case true => Ok(bs._2)
          case _ => BadRequest(bs._2)
        }
      }
    }
  }

  /**
   * do the work of PUT registration
   */
  def updateCallbackregistration(id: Long, request: Request[AnyContent]): (Boolean, String) = {
    val jsonBody: Option[JsValue] = request.body.asJson
    play.api.Logger.debug("putCallbackregistration " + id + " " + request.body)
    jsonBody match {
      case None => { (false, "Could not resolve request body as json.  Do you have the 'Content-type' header set to 'application/json'? " + request.body) }
      case _ => { (true, Json.stringify(jsonBody.get)) } // gives back received json for now
    }
  }

}