package controllers

import models._
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Request
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._

object Register extends Controller with Secured {

  /** GET / */
  def index = Action.async {
    val f: Future[String] = Future { "Hello" }
    f.map(s => Ok(s))
  }

  /**
   * GET the customer's callback configuration.
   *  id is the customer id.
   */
  def getCallbackRegistration(id: Long) = withBearerTokenAuthAsync { t =>
    request =>
      val f: Future[Option[String]] = Future {
        val ocb: Option[Callback] = CallbackCatalog.catalog.get(id).getOrElse(None)
        ocb.isDefined match {
          case false => None
          case _ => {
            val j: JsValue = Json.toJson(ocb.get) // {"consumerId":99,"url":"https://notaurl99.net/mycallback/response"}
            Some(Json.stringify(j))
          }
        }
      }
      f.map(os =>
        os.isDefined match {
          case true => Ok(os.get)
          case _ => Ok("No Callback found for consumer id " + id)
        })
  }

  /**
   * PUT the customer's callback configuration.
   *  id is the customer id.
   *
   *  data expected
   *  {"consumerId":99,"url":"https://notaurl99.net/mycallback"}
   */
  def putCallbackRegistration(id: Long) = withBearerTokenAuthAsync { t =>
    request =>
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
          val oCallback: Option[Callback] = resolveCallback(jsonBody.get)
          oCallback.isDefined match {
            case false => (false, "Could not reconcile the structure of the json data provided in the request. Please see api documentation. " + jsonBody.get)
            case _ => {
              CallbackCatalog.saveCallback(oCallback.get)
              (true, Json.stringify(jsonBody.get)) // send the same json from the body back
            }
          }
        } catch {
          case e: Exception => (false, e.getMessage)
        }
      }
    }
  }

  /** */
  def resolveCallback(j: JsValue): Option[Callback] = {
    Json.fromJson[Callback](j).asOpt
  }

}