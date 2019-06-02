package io.taskmonk.entities

import play.api.libs.json.Json

case class NewBatchData(batch_name: String, priority: Option[Int] = Some(1), comments: Option[String] = None,
                           notifications: List[NotificationScala])

object NewBatchData {
  implicit val reads = Json.reads[NewBatchData]
  implicit val writes = Json.writes[NewBatchData]
}
case class NewBatchContent(content: String, batch_name: String, priority: Option[Int] = Some(1), comments: Option[String] = None,
                           notifications: List[NotificationScala])

object NewBatchContent {
  implicit val reads = Json.reads[NewBatchContent]
  implicit val writes = Json.writes[NewBatchContent]
}
