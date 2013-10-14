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

/** {"consumerId":99,"url":"https://notaurl99.net/mycallback/response"} */
case class Callback(consumerId: Long, url: String) {}
object Callback extends JsonParsable[Callback] {
  implicit val jWriter = Json.writes[Callback] // Json.toJson(obj): JsValue
  implicit val jReader = Json.reads[Callback] // Json.fromJson[Callback](jsval): JsResult[Callback] .asOpt Option[Callback]
  
  val empty: Callback = Callback(0L, "")
  
  /** explicit conversion using Json.obj */
  def toJsValue(obj: Callback): JsValue = {
    Json.obj(
      "consumerId" -> JsNumber(obj.consumerId),
      "url" -> obj.url)
  }
  /** explicit parsing from JsValue */
  def fromJsValue(j: JsValue): Option[Callback] = {
    val consumerId: JsValue = j \ "consumerId"
    val url: JsValue = j \ "url"
    try {
      val r = Callback(consumerId.as[Long], url.as[String])
      Some(r)
    } catch {
      case e: play.api.libs.json.JsResultException => None
    }
  }
}

/** the database */
object CallbackCatalog {
  val catalog: scala.collection.mutable.Map[Long, Option[Callback]] = scala.collection.mutable.Map((0L -> None))
  
  /** */
  def saveCallback(callback: Callback) = {
    catalog.put(callback.consumerId, Some(callback))
  }
}

/*
RFC 1738 encoded consumer key (does not change)	vcncb4TTuPPTEGLOSKIex
RFC 1738 encoded consumer secret (does not change)	L9yyTYyQg9ieKLOPhWolMNVvKUUw8iE7777777yOg
Bearer token credentials:	vcncb4TTuPPTEGLOSKIex:L9yyTYyQg9ieKLOPhWolMNVvKUUw8iE7777777yOg
Base64 encoded bearer token credentials:  ---
*/
case class Consumer(id: Long, name: String, key: String, secret: String) {
  def buildCredentials: String = Consumer.buildCredentialsFromKeySecret(key, secret)
  /** don't show key secret */
  def toGetJson: String = { Json.obj("id" -> id, "name" -> name).toString }
    
//  def buildCredentialsBase64: String = {
//    //org.apache.commons.codec.binary.Base64.encodeBase64(x$1)
//    getCredentials
//  }
}
object Consumer {
  def buildCredentials(consumer: Consumer): String = { buildCredentialsFromKeySecret(consumer.key, consumer.secret) }
  def buildCredentialsFromKeySecret(k: String, sec: String) = { k + ":" + sec }
  /** returns tuple (key, secret) */
  def splitCredentials(cred: String): (String, String) = { 
    val sa = cred.split(":") 
    (sa(0), sa(1))
  }
}
object ConsumerCatalog {
   val ID_SIMULATED_CONSUMER: Long = 1L
   val consumers: scala.collection.mutable.Map[Long, Consumer] = scala.collection.mutable.Map((ID_SIMULATED_CONSUMER -> Consumer(1L, "Simulator Co.", "vcncb4TTuPPTEGLOSKIex", "L9yyTYyQg9ieKLOPhWolMNVvKUUw8iE7777777yOg")))
   val tokens: scala.collection.mutable.Map[Long, Option[String]] = scala.collection.mutable.Map((ID_SIMULATED_CONSUMER -> None))
   
   def getConsumerSimulated: Option[Consumer] = {
     ConsumerCatalog.consumers.get(ID_SIMULATED_CONSUMER)
   }
   
   def getConsumerByCredentials(cred: String): Option[Consumer] = {
     val filtered = consumers.values.filter(x => x.buildCredentials == cred)
     filtered.size match {
       case 0 => None
       case _ => { filtered.headOption }
     }
   }
   
   def authorizeToken(t: String): Option[String] = {
     tokens.values.exists(x => x.getOrElse("***") == t) match {
       case false => None
       case _ => { Some(t) }
     }
   }
   
   def authorizeConsumerCredential(cred: String): Option[String] = {
     consumers.values.exists(x => x.buildCredentials == cred) match {
       case false => None
       case _ => { Some(cred) }
     }
   }
}


