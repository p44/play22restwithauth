package models

import org.specs2.mutable._

object ModelsSpec extends Specification {

  //sbt > test-only models.ModelsSpec
  
  import util._
  
  "Models" should {
    "Consumer" in {
      val oc: Option[Consumer] = ConsumerCatalog.consumers.get(1L)
      oc mustNotEqual None
      oc.get.id mustEqual 1L
    }
    "ConsumerCatalog.tokens" in {
      val bearerToken = GeneratorUtil.generateBearerToken
      ConsumerCatalog.tokens.put(99L, Some(bearerToken))
      ConsumerCatalog.authenticateToken(bearerToken) mustNotEqual None
      val g: Option[String] = ConsumerCatalog.tokens.get(99L).getOrElse(None)
      g mustNotEqual None
      g.get mustEqual bearerToken
    }
  }

}