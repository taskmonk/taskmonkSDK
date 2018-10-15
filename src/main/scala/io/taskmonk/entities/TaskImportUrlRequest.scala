package io.taskmonk.entities

import play.api.libs.json.Json

case class TaskImportUrlRequest(fileURL: String, batchName: String)
object TaskImportUrlRequest {
  implicit val reads = Json.reads[TaskImportUrlRequest]
  implicit val writes = Json.writes[TaskImportUrlRequest]
}

case class TaskImportUrlResponse(batchId: String, jobId: String)
object TaskImportUrlResponse {
  implicit val reads = Json.reads[TaskImportUrlResponse]
  implicit val writes = Json.writes[TaskImportUrlResponse]
}
