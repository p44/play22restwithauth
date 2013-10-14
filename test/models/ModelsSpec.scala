package models

import util._
import org.specs2.mutable._
import play.api.libs.json._

object ModelsSpec extends Specification {

  //sbt > test-only models.ModelsSpec
  
  "Models" should {
    "Consumer" in {
      val oc: Option[Consumer] = ConsumerCatalog.consumers.get(1L)
      oc mustNotEqual None
      oc.get.id mustEqual 1L
    }
    
    "Callback json" in {
      val cbUrl = "https://notaurl99.net/mycallback/response"
      val cb = Callback(99L, cbUrl)
      val jsval1: JsValue = Json.toJson(cb)
      val jsval2: JsValue = Callback.toJsValue(cb)
      println("Callback jsval1 "+ jsval1 + " jsval2 " + jsval2)
      jsval1 mustEqual jsval2
      val cbParsed1 = Json.fromJson[Callback](jsval1).asOpt.getOrElse(Callback.empty)
      val cbParsed2 = Callback.fromJsValue(jsval2).getOrElse(Callback.empty)
      println("Callback cbParsed1 "+ cbParsed1 + " cbParsed2 " + cbParsed2)
      cbParsed1 mustNotEqual Callback.empty
      cbParsed1 mustEqual cbParsed2
    }
    
    "ConsumerCatalog.tokens" in {
      val bearerToken = GeneratorUtil.generateBearerToken
      ConsumerCatalog.tokens.put(99L, Some(bearerToken))
      ConsumerCatalog.authorizeToken(bearerToken) mustNotEqual None
      val g: Option[String] = ConsumerCatalog.tokens.get(99L).getOrElse(None)
      g mustNotEqual None
      g.get mustEqual bearerToken
    }
    
    "ConsumerCatalog.authorizeConsumerCredential" in {
      // vcncb4TTuPPTEGLOSKIex:L9yyTYyQg9ieKLOPhWolMNVvKUUw8iE7777777yOg
      val oc: Option[Consumer] = ConsumerCatalog.getConsumerSimulated
      oc mustNotEqual None
      val cred = oc.get.buildCredentials
      println("ConsumerCatalog.authorizeConsumerCredential cred " + cred)
      val authorizedCred = ConsumerCatalog.authorizeConsumerCredential(cred)
      authorizedCred mustNotEqual None
    }
  }

}