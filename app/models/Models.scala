package models

import play.api.libs.json._

/**
 * 
 */
object Models {
}

trait JsonParsable[T] {
  def toJsValue(obj: T): JsValue
  def fromJsValue(j: JsValue): Option[T]
}

case class Callback(customerId: Long, url: String) {}
object Callback extends JsonParsable[Callback] {
  implicit val jWriter = Json.writes[Callback] // Json.toJson(obj): JsValue
  implicit val jReader = Json.reads[Callback] // Json.fromJson[Callback](jsval): JsResult[Callback] .asOpt Option[Callback]
  
  /** explicit conversion using Json.obj */
  def toJsValue(obj: Callback): JsValue = {
    Json.obj(
      "id" -> JsNumber(obj.customerId),
      "url" -> obj.url)
  }
  /** explicit parsing from JsValue */
  def fromJsValue(j: JsValue): Option[Callback] = {
    val id: JsValue = j \ "id"
    val url: JsValue = j \ "url"
    try {
      val r = Callback(id.as[Long], url.as[String])
      Some(r)
    } catch {
      case e: play.api.libs.json.JsResultException => None
    }
  }
}

/** the database */
object CallbackCatalog {
  val catalog = scala.collection.mutable.Map((0L -> "None"))
  
  /** */
  def saveCallback(callback: Callback) = {
    catalog.put(callback.customerId, callback.url)
  }
}