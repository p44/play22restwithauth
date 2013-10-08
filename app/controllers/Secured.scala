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
  
  val AUTHORIZATION_TOKEN_KEY = "Authorization"

  def onUnauthorized(request: RequestHeader) = Unauthorized("Authorization Failure")
  def authorizationBearerToken(request: RequestHeader): Option[String] = {
    val os = request.headers.get(AUTHORIZATION_TOKEN_KEY)
    os.isDefined match {
      case false => None
      case true =>  os // TODO check the token for "Bearer ", strip and return token
    }
  }
  def authorizationBasicToken(request: RequestHeader): Option[String] = {
    request.headers.get(AUTHORIZATION_TOKEN_KEY)
  }
  def bearerTokenAthenticated(request: RequestHeader): Option[String] = {
    val ot = authorizationBearerToken(request)
    ot.isDefined match { // it exists in the header and is marked a bearer token
      case false => None
      case _ => {
        true match { // TODO check the data store for the token value
          case false => None
          case _ => ot
        }
      }
    }
  }
  
}