package util

import org.specs2.mutable._

object GeneratorUtilSpec extends Specification {

  //sbt > test-only util.GeneratorUtilSpec
  
  "GeneratorUtil" should {
    "generateBearerToken" in {
      val s = GeneratorUtil.generateBearerToken
      println("generateBearerToken s " + s)
      s.length mustEqual 32
    }
  }

}