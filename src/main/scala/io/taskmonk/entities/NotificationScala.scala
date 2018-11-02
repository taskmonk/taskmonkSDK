package io.taskmonk.entities

import play.api.libs.json.Json

case class Notification(notificationType: String, metaData: java.util.Map[String, String])
case class NotificationScala(notificationType: String, metaData: Map[String, String])
object NotificationScala {
  implicit val reads = Json.reads[NotificationScala]
  implicit val writes = Json.writes[NotificationScala]
}
