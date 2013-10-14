package controllers

import models._
import play.api.mvc.Action
import play.api.mvc.Request
import play.api.mvc.RequestHeader
import play.api.mvc.Results.Unauthorized
import play.api.mvc.Security
import play.api.mvc.Result
import play.api.mvc.AnyContent

trait Secured {

  val AUTHORIZATION_KEY = "Authorization"

  def onUnauthorized(request: RequestHeader) = Unauthorized("Authorization Failure")
  def authorizedBearerToken(request: RequestHeader): Option[String] = {
    val os = request.headers.get(AUTHORIZATION_KEY)
    play.api.Logger.debug("authorizedBearerToken os " + os)
    os.isDefined match {
      case false => None
      case true => {
        val ot: Option[String] = SecurityHelper.parseBearerToken(os.get)
        ConsumerCatalog.authorizeToken(ot.getOrElse("NotAToken"))
      }
    }
  }
  def authorizedConsumerCredential(request: RequestHeader): Option[String] = {
    val os = request.headers.get(AUTHORIZATION_KEY)
    play.api.Logger.debug("authorizedConsumerCredential os " + os)
    os.isDefined match {
      case false => None
      case true => {
        val oCred: Option[String] = SecurityHelper.parseBasicToken(os.get)
        ConsumerCatalog.authorizeConsumerCredential(oCred.getOrElse("NotACredential"))
      }
    }
  }
  
  /** Action wrapper - Enforces valid bearer token in the header */
  def withBearerTokenAuth(f: => String => Request[AnyContent] => Result) = { 
    Security.Authenticated(authorizedBearerToken, onUnauthorized) { token =>
      Action(request => f(token)(request))
    }
  }
  /** Authorization: Basic eHZ6MWV2R ... o4OERSZHlPZw== */
  def withConsumerCredentialAuth(f: => String => Request[AnyContent] => Result) = { 
    Security.Authenticated(authorizedConsumerCredential, onUnauthorized) { token =>
      Action(request => f(token)(request))
    }
  }

}

object SecurityHelper {

  def parseBearerToken(authHeader: String): Option[String] = { parseAuthToken("bearer ", authHeader) }
  def parseBasicToken(authHeader: String): Option[String] = { parseAuthToken("basic ", authHeader) }

  def parseAuthToken(prefixLower: String, authHeader: String): Option[String] = {
    val s = authHeader.trim
    s.toLowerCase.contains(prefixLower) match { // e.g. "bearer "
      case false => None
      case true => { splitAuthToken(s) }
    }
  }

  def splitAuthToken(s: String): Option[String] = {
    val bt: Array[String] = s.split(" ")
    bt.size > 0 match {
      case true => Some(bt(1))
      case _ => None
    }
  }

}