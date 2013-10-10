package controllers

import models._
import util._
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Request
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.Logger

/**
 * Based upon the spec and twitter's implementation
 * http://tools.ietf.org/html/rfc6749#section-4.4
 * https://dev.twitter.com/docs/auth/application-only-auth
 */
object OAuth2 extends Controller {
  
  /**
   * Must be https encoded
   * e.g. our model is https://dev.twitter.com/docs/api/1.1/post/oauth2/token
   * 
   * returns a token
   * {"token_type":"bearer","access_token":"ABCDEFGHIJKLMNOP"}
   */
  def postToken = Action { request =>
    // Get headers and verify (Secured wrapper to verify by client 
    // From json get: grant_type=client_credentials
    //ConsumerCatalog.getConsumerByCredentials(cred)
    val id: Long = 1L
    val bearerToken = GeneratorUtil.generateBearerToken
    ConsumerCatalog.tokens.put(id, Some(bearerToken))
    val j = Json.obj("token_type" -> "bearer", "access_token" -> bearerToken).toString
    Logger.info("postToken for id " + id + "  " + j)
    Ok(j)
  }
  
  /**
   * Must be https encoded
   * e.g. our model is https://dev.twitter.com/docs/api/1.1/post/oauth2/invalidate_token
   * 
   * Returns the revoked token
   * {"access_token":"ABCDEFGHIJKLMNOP"}
   */
  def postInvalidateToken = Action { request =>
    // Get headers
    // From json get: access_token=ABCDEFGHIJKLMNOP
    Ok("""{"access_token":"ABCDEFGHIJKLMNOP"}""") // return the revoked token
  }

}