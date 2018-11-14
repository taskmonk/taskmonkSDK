package io.taskmonk.entities

import play.api.libs.json.Json

case class BatchSummaryV2(newCount: String, inProgress: String, completed: String, total: Int, jobId: Option[String], fileUrl: Option[String])

object BatchSummaryV2 {

  implicit val writes = Json.writes[BatchSummaryV2]
  implicit val reads = Json.reads[BatchSummaryV2]
}

