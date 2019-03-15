package io.taskmonk.auth

import com.google.inject.ImplementedBy
import com.softwaremill.sttp.playJson.asJson
import com.softwaremill.sttp.{Empty, RequestT, SttpApi, Uri}
import exceptions.ApiFailedException
import io.taskmonk.entities.TaskImportUrlResponse
import play.api.libs.json.Json

import scala.concurrent.Future

@ImplementedBy(classOf[OAuthClientCredentials])
trait Credentials {
  def addAuthInfo[U[_], T, S](request: RequestT[U, T, S]): RequestT[U, T, S]
  def addAuthInfo(uri: Uri): Uri


}

//class ApiKeyCredentials(apiKey: String) extends Credentials {
//  def addAuthInfo[U[_], T, S] (request: RequestT[U, T, S]): RequestT[U, T, S] =  {
//    request.header("Authorization",  s"API_KEY ${apiKey}")
//  }
//}

class OAuthClientCredentials(clientId: String, clientSecret: String) extends Credentials {
  var accessToken: Option[String] = None
  var refreshToken: Option[String] = None
  def addAuthInfo[U[_], T, S] (request: RequestT[U, T, S]): RequestT[U, T, S] =  {
    request
  }

  override def addAuthInfo(uri: Uri): Uri = {
    uri.params(("grant_type", "client_credentials"), ("client_id", clientId), ("client_secret", clientSecret))
  }
}

class OAuthTokenCredentials(tokenResponse: TokenResponse) extends Credentials {
  def addAuthInfo[U[_], T, S] (request: RequestT[U, T, S]): RequestT[U, T, S] =  {
    request.auth.bearer(tokenResponse.access_token)
  }

  override def addAuthInfo(uri: Uri): Uri = uri
}


