package io.taskmonk.entities

import java.util
import java.util.Date

import play.api.libs.json.Json

import scala.collection.JavaConverters._


object Task {

  implicit val reads = Json.reads[Task]
  implicit val writes = Json.writes[Task]
}
case class Task(externalId: String,
                project_id: String,
                batch_id: String,
                input: Map[String, String],
                output: Map[String, String]) {
  def this(externalId: String,
            project_id: String,
            batch_id: String,
            input: java.util.Map[String, String]) = {
    this(externalId, project_id, batch_id, input.asScala.toMap, Map.empty[String, String])
  }
  def getInput(): java.util.Map[String, String] = {
    input.asJava
  }
  def getOutput(): java.util.Map[String, String] = {
    output.asJava
  }
  def getExternalId(): String = {
    externalId
  }
}


