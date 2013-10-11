package util

import scala.util._
import java.security.SecureRandom

/** Generate stuff */
object GeneratorUtil {

  val POSSIBLE_CHARS: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZ-._0123456789abcdefghijklmnopqrstuvwxyz"
  val secRandom: java.security.SecureRandom = new java.security.SecureRandom()

  /** 32 char bearer token per spec RFC6750 (http://http://tools.ietf.org/html/rfc6750) */
  def generateBearerToken: String = { makeStringOfPossibleChars(32) }

  /**
   * uses recursion to assemble a String of random characters from POSSIBLE_CHARS
   */
  def makeStringOfPossibleChars(len: Int, tokenBase: String = ""): String = {
    (len <= 0) match {
      case true => tokenBase
      case _ => {
        val newBase = tokenBase + POSSIBLE_CHARS(secRandom.nextInt(POSSIBLE_CHARS.length))
        makeStringOfPossibleChars(len - 1, newBase)
      }
    }
  }
}

