package io.taskmonk.entities

import play.api.libs.json.Json

case class Notification(notificationType: String, metaData: Map[String, String])
object Notification {
  implicit val reads = Json.reads[Notification]
  implicit val writes = Json.writes[Notification]
}
