package io.taskmonk.entities

import java.util.Date

import play.api.libs.json.Json


object Task {

  implicit val reads = Json.reads[Task]
  implicit val writes = Json.writes[Task]
}
case class Task(externalId: String,
                project_id: String,
                batch_id: String,
                input: Map[String, String],
                output: Map[String, String])


