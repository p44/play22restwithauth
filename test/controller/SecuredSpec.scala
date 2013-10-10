package controllers

import org.specs2.mutable._

object SecuredSpec extends Specification {

  //sbt > test-only controllers.SecuredSpec
  
  import util._

  "Secured" should {
    "SecurityHelper.parseBearerToken" in {
      val genToken = GeneratorUtil.generateBearerToken
      val testBearerToken = "Bearer " + genToken
      val oParsed: Option[String] = SecurityHelper.parseBearerToken(testBearerToken)
      println("parseBearerToken oParsed " + oParsed)
      oParsed.isDefined mustEqual true
      oParsed.get mustEqual genToken
    }
  }
}