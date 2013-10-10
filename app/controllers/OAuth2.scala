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
object OAuth2 extends Controller with Secured {

  /**
   * Must be https encoded
   * e.g. our model is https://dev.twitter.com/docs/api/1.1/post/oauth2/token
   *
   * Auth by Consumer credentials in the header using Basic auth.
   * Returns a token
   * {"token_type":"bearer","access_token":"ABCDEFGHIJKLMNOP"}
   */
  def postToken = withConsumerCredentialAuth { cred =>
    { request =>
      val oc = ConsumerCatalog.getConsumerByCredentials(cred)
      oc.isDefined match {
        case false => InternalServerError("Consumer lookup issues exist")
        case true => {
          val id: Long = oc.get.id
          val bearerToken = GeneratorUtil.generateBearerToken
          ConsumerCatalog.tokens.put(id, Some(bearerToken))
          val j = Json.obj("token_type" -> "bearer", "access_token" -> bearerToken).toString
          Logger.info("postToken for id " + id + "  " + j)
          Ok(j)
        }
      }
    }
  }

  /**
   * Must be https encoded
   * e.g. our model is https://dev.twitter.com/docs/api/1.1/post/oauth2/invalidate_token
   *
   * Auth by Consumer credentials in the header using Basic auth.
   * Returns the revoked token
   * {"access_token":"ABCDEFGHIJKLMNOP"}
   */
  def postInvalidateToken = withConsumerCredentialAuth { cred =>
    { request =>
      val oc = ConsumerCatalog.getConsumerByCredentials(cred)
      oc.isDefined match {
        case false => InternalServerError("Consumer lookup issues exist")
        case true => {
          val optJsonValue: Option[JsValue] = request.body.asJson
          optJsonValue match {
            case None => BadRequest("Could not resolve request body as json.  Do you have the 'Content-type' header set to 'application/json'? " + request.body)
            case _ => {
              val optToken: Option[String] = getTokenFromJson(optJsonValue.get)
              optToken.isDefined match {
                case false => BadRequest("Did not find a token to invalidate.  Did you set the json content for \"access_token\"? " + request.body)
                case true => {
                  val id: Long = oc.get.id
                  ConsumerCatalog.tokens.put(id, None) // invalidate the token
                  val j = Json.obj("access_token" -> optToken.get).toString
                  Ok(j) // return the revoked token
                }
              }
            }
          }
        }
      }
    }
  }

  def getTokenFromJson(jv: JsValue): Option[String] = {
    (jv \ "access_token").asOpt[String]
  }

}