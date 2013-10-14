package controllers

import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Request
import play.api.libs.EventSource
import play.api.libs.json._
import play.api.libs.iteratee.{ Concurrent, Enumeratee, Enumerator }

/**
 * Simulates a Consumer of the registration service
 */
object Simulator extends Controller {
  
  val (alarmOut, alarmChannel) = Concurrent.broadcast[JsValue]

  def index = Action {
    Ok(views.html.simulator.render)
  }

  /**
   * Unsecure PUT takes alarm json or any json for that matter and feeds it to a broadcast channel
   * Alarm {"consumerId":99,"level":"emergency"}
   */
  def putAlarm = Action { request =>
    play.api.Logger.debug("Simulator.putAlarm " + request.body)
    val jsonBody: Option[JsValue] = request.body.asJson
    jsonBody match {
      case None => {
        play.api.Logger.debug("Simulator.putAlarm - bad json " + request.body)
        BadRequest("Not Json " + request.body)
      }
      case _ => {
        play.api.Logger.debug("Simulator.putAlarm - adding to alarmChannel")
        alarmChannel.push(jsonBody.get); // publish to the channel
        Ok("Got it.")
      }
    }
  }
  
  
  /** watch for disconnect */
  def connDeathWatch(addr: String): Enumeratee[JsValue, JsValue] = {
    Enumeratee.onIterateeDone { () =>
      println(addr + " - feed disconnected")
    }
  }
  
  /** Controller action serving activity all (no filter) */
  def liveStatusFeedAll = Action { request =>
    play.api.Logger.info("FEED alarmOut - " + request.remoteAddress + " - alarmOut connected")
    Ok.chunked(alarmOut
      &> Concurrent.buffer(50)
      &> connDeathWatch(request.remoteAddress)
      &> EventSource()).as("text/event-stream")
  }

}