package io.taskmonk.entities

import play.api.libs.json.Json

case class JobProgressResponseScala(completed: Int, total: Int, percentage: Int)
object  JobProgressResponseScala {
  implicit val reads = Json.reads[JobProgressResponseScala]
  implicit val writes = Json.writes[JobProgressResponseScala]
}
