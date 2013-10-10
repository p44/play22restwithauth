package models

import util._
import play.api.libs.json._

/**
 * 
 */
object Models {}

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

/*
RFC 1738 encoded consumer key (does not change)	xvz1evFS4wEEPTGEFPHBog
RFC 1738 encoded consumer secret (does not change)	L8qq9PZyRg6ieKGEKhZolGC0vJWLw8iEJ88DRdyOg
Bearer token credentials:	xvz1evFS4wEEPTGEFPHBog:L8qq9PZyRg6ieKGEKhZolGC0vJWLw8iEJ88DRdyOg
Base64 encoded bearer token credentials:  eHZ6MWV2RlM0d0VFUFRHRUZQSEJvZzpMOHFxOVBaeVJnNmllS0dFS2hab2xHQzB2SldMdzhpRUo4OERSZHlPZw==
*/
case class Consumer(id: Long, name: String, key: String, secret: String) {
  def buildCredentials: String = Consumer.buildCredentialsFromKeySecret(key, secret)
//  def buildCredentialsBase64: String = {
//    //org.apache.commons.codec.binary.Base64.encodeBase64(x$1)
//    getCredentials
//  }
}
object Consumer {
  def buildCredentials(consumer: Consumer): String = { buildCredentialsFromKeySecret(consumer.key, consumer.secret) }
  def buildCredentialsFromKeySecret(k: String, sec: String) = { k + " :" + sec }
  def splitCredentials(cred: String) = { 
    val sa = cred.split(":") 
    (sa(0), sa(1))
  }
}
object ConsumerCatalog {
   val consumers: scala.collection.mutable.Map[Long, Consumer] = scala.collection.mutable.Map((1L -> Consumer(1L, "Simulator Co.", "vcncb4TTuPPTEGLOSKIex", "L9yyTYyQg9ieKLOPhWolMNVvKUUw8iE7777777yOg")))
   val tokens: scala.collection.mutable.Map[Long, Option[String]] = scala.collection.mutable.Map((1L -> None))
   
   def getConsumerByCredentials(cred: String): Option[Consumer] = {
     val filtered = consumers.values.filter(x => x.buildCredentials == cred)
     filtered.size match {
       case 0 => None
       case _ => { filtered.headOption }
     }
   }
   
   def authenticateToken(t: String): Option[String] = {
     tokens.values.exists(x => x.getOrElse("***") == t) match {
       case false => None
       case _ => { Some(t) }
     }
   }
   
   def authenticateConsumerCredential(cred: String): Option[String] = {
     consumers.values.exists(x => x.buildCredentials == cred) match {
       case false => None
       case _ => { Some(cred) }
     }
   }
}


