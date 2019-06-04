package io.taskmonk.entities

import play.api.libs.json.Json

case class BatchOutput(fileUrl: String, jobId: String)
object BatchOutput {
  implicit val reads = Json.reads[BatchOutput]
  implicit val writes = Json.writes[BatchOutput]
}

case class BatchSummaryScala(new_count: Int, in_progress: Int, completed: Int, total: Int, job_id: Option[String], file_url: Option[String]) {
  def isBatchComplete(): Boolean = {
    return (completed == total)
  }
}
object BatchSummaryScala {

  implicit val writes = Json.writes[BatchSummaryScala]
  implicit val reads = Json.reads[BatchSummaryScala]
}

