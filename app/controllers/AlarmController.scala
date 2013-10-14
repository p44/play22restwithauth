package controllers

import models._
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Request
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.libs.ws.WS
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object AlarmController extends Controller with Secured {
  
  val HEADER_CONTENT_TYPE_JSON: (String, String) = ("Content-Type", "application/json")

  /**
   * PUT an Alarm which triggers a callback if one exists for the consumer id specified
   * the alarm data is passed to the callback.
   * id is the consumer id.
   *
   *  data expected
   *  Alarm {"consumerId":99,"level":"emergency"}
   */
  def putAlarm(id: Long) = withBearerTokenAuthAsync { t =>
    request =>
      val f: Future[(Boolean, String)] = Future { processAlarm(id, request) }
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
  def processAlarm(id: Long, request: Request[AnyContent]): (Boolean, String) = {
    val ocb: Option[Callback] = CallbackCatalog.catalog.get(id).getOrElse(None)
    ocb.isDefined match {
      case false => (false, "No callback registration is found for consumer id " + id)
      case _ => {
        val jsonBody: Option[JsValue] = request.body.asJson
        play.api.Logger.debug("processAlarm " + id + " " + request.body)
        jsonBody match {
          case None => { (false, "Could not resolve request body as json.  Do you have the 'Content-type' header set to 'application/json'? " + request.body) }
          case _ => {
            (sendToCallback(ocb.get.url, jsonBody.get), "Callback initiated")
          }
        }
      }
    }
  }

  /** Call the URL send the alarm - better to use an actor here in a production app. */
  def sendToCallback(url: String, jv: JsValue): Boolean = {
    //val call: WS.WSRequestHolder = WS.url(url)
    val j = Json.stringify(jv)
    play.api.Logger.debug("sendToCallback PUT call " + url + " " + j)
    val r: scala.concurrent.Future[play.api.libs.ws.Response] =
      WS.url(url).withHeaders(HEADER_CONTENT_TYPE_JSON)put(j)
    val s = Await.result(r, Duration("45 sec")).body.toString()
    play.api.Logger.debug("sendToCallback result " + s)
    true
  }
}