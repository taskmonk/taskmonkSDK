package io.taskmonk.integrations.streaming

import io.taskmonk.entities.Task
import io.taskmonk.integrations.azure.servicebus.MessageHandler
import play.api.libs.json.{JsError, JsSuccess, Json}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class TaskStreamer (config: java.util.Map[String, String]) extends TaskStreamerScala(config) {
  val duration = 60 seconds
  def sendSync(task: Task)  = {
    Await.result(send(task), duration)
  }

}
