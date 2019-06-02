package io.taskmonk.entities

import play.api.libs.json.Json

case class TaskImportUrlRequest(fileURL: String, batchName: String)
object TaskImportUrlRequest {
  implicit val reads = Json.reads[TaskImportUrlRequest]
  implicit val writes = Json.writes[TaskImportUrlRequest]
}

case class TaskImportUrlResponseScala(batchId: String, jobId: String)
object TaskImportUrlResponseScala {
  implicit val reads = Json.reads[TaskImportUrlResponseScala]
  implicit val writes = Json.writes[TaskImportUrlResponseScala]
}
