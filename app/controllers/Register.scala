package controllers

import models._
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
  def getCallbackRegistration(id: Long) = Action.async { request =>
    val f: Future[Option[String]] = Future {
      val ocb = CallbackCatalog.catalog.get(id)
      ocb.isDefined match {
        case false => None
        case _ => {
          val j: JsValue = Json.toJson(ocb.get)
          Some(Json.stringify(j))
        }
      }
    }
    f.map(os =>
      os.isDefined match {
        case true => Ok(os.get)
        case _ => Ok("None Found for " + id)
      })
  }

  /**
   * PUT the customer's callback configuration.
   *  id is the customer id.
   */
  def putCallbackRegistration(id: Long) = Action.async { request =>
    val f: Future[(Boolean, String)] = Future { updateCallbackRegistration(id, request) }
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
  def updateCallbackRegistration(id: Long, request: Request[AnyContent]): (Boolean, String) = {
    val jsonBody: Option[JsValue] = request.body.asJson
    play.api.Logger.debug("putCallbackregistration " + id + " " + request.body)
    jsonBody match {
      case None => { (false, "Could not resolve request body as json.  Do you have the 'Content-type' header set to 'application/json'? " + request.body) }
      case _ => {
        try {
          val callback: Callback = resolveCallback(jsonBody.get)
          CallbackCatalog.saveCallback(callback)
          (true, Json.stringify(jsonBody.get))
        } catch {
          case e: Exception => (false, e.getMessage)
        }
      }
    }
  }

  /** */
  def resolveCallback(j: JsValue): Callback = {
    val id: Long = (j \ "id").as[Long]
    val url: String = (j \ "url").as[String]
    val r: Callback = new Callback(id, url)
    r
  }

}