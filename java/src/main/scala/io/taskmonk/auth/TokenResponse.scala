package io.taskmonk.auth

import play.api.libs.json.Json

case class TokenResponse(token_type: String, access_token: String, refresh_token: String, expires_in: Option[Long]) {
  def isExpired = true
}
object TokenResponse {
  implicit val reads = Json.reads[TokenResponse]
  implicit val writes = Json.writes[TokenResponse]
}
