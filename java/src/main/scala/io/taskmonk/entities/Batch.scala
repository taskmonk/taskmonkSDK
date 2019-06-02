package io.taskmonk.entities

import play.api.libs.json.Json

case class BatchOutput(fileUrl: String, jobId: String)
object BatchOutput {
  implicit val reads = Json.reads[BatchOutput]
  implicit val writes = Json.writes[BatchOutput]
}
case class BatchSummaryV2(newCount: String, inProgress: String, completed: String, total: Int, jobId: Option[String], fileUrl: Option[String])

object BatchSummaryV2 {

  implicit val writes = Json.writes[BatchSummaryV2]
  implicit val reads = Json.reads[BatchSummaryV2]
}

