package io.taskmonk.integrations.streaming

import io.taskmonk.entities.TaskScala
import io.taskmonk.integrations.azure.servicebus.{MessageHandler, ServiceBusListener, ServiceBusSendInterface}
import io.taskmonk.utils.SLF4JLogging
import play.api.libs.json.{JsError, JsSuccess, Json}

import scala.concurrent.Future

class MessageStreamWriter(queueName: String, accessKey: String) extends ServiceBusSendInterface(queueName, accessKey) {
  override def send(messageId: String, label: String, content: String): Future[_] = {
    super.send(messageId, label, content)
  }
}

class MessageStreamListener(queueName: String, accessKey: String) extends ServiceBusListener(queueName, accessKey) {
}

trait TaskListener {
  def onTaskReceived(task: TaskScala)
}

class TaskStreamerListener(queueName: String, accessKey: String)  extends SLF4JLogging {
  val messageStreamListener: MessageStreamListener = new MessageStreamListener(queueName, accessKey)
  def addListener(taskListener: TaskListener): Boolean = {
    messageStreamListener.addMessageHandler(new MessageHandler {
      override def handle(message: String): Unit = {
        val task = Json.parse(message).validate[TaskScala]
        task match {
          case e: JsError =>
            log.error(JsError.toJson(e).toString())
          case s: JsSuccess[TaskScala] =>
            log.debug("Sending task {} to listener", s.get.externalId)
            taskListener.onTaskReceived(s.get)
        }
      }
    })
  }
}

class TaskStreamerSender(queueName: String, accessKey: String)  extends SLF4JLogging {
  val messageStreamWriter: MessageStreamWriter = new MessageStreamWriter(queueName, accessKey)
  def send(task: TaskScala): Future[_] = {
    messageStreamWriter.send(task.externalId, task.project_id + ":" + task.batch_id, Json.toJson(task).toString)
  }
}
