package io.taskmonk.entities

import play.api.libs.json.Json

case class JobProgressResponse(completed: Int, total: Int, percentage: Int)
object  JobProgressResponse {
  implicit val reads = Json.reads[JobProgressResponse]
  implicit val writes = Json.writes[JobProgressResponse]
}
