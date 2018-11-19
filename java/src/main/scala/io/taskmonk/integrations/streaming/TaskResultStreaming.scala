package io.taskmonk.integrations.streaming

import io.taskmonk.entities.Task
import io.taskmonk.integrations.azure.servicebus.{MessageHandler, ServiceBusListener, ServiceBusSendInterface}
import io.taskmonk.utils.SLF4JLogging
import play.api.libs.json.{JsError, JsSuccess, Json}

import scala.collection.JavaConverters._
import scala.concurrent.Future

class MessageStreamWriter(config: Map[String, String]) extends ServiceBusSendInterface(config) {
  override def send(messageId: String, content: String, label: String): Future[_] = {
    super.send(messageId, content, label)
  }
}

class MessageStreamListener(config: Map[String, String]) extends ServiceBusListener(config) {
}

trait TaskListener {
  def onTaskReceived(task: Task)
}

/**
  * Class used for streaming tasks.
  * @param config
  *               The config parameters to be passed in.
  *               authListenString - The authentication string used for listening of tasks
  *               authWriteString - The authentication sting used for writing tasks to the stream
  *               topic - The topic used for streaming
  *               subsId - The subscription id used for streaming
  */
class TaskStreamerScala(config: Map[String, String])  extends SLF4JLogging {
  def this(config: java.util.Map[String, String]) = this(config.asScala.toMap)

  val messageStreamWriter: MessageStreamWriter = new MessageStreamWriter(config)
  val messageStreamListener: MessageStreamListener = new MessageStreamListener(config)
  def send(task: Task): Future[_] = {
    messageStreamWriter.send(task.id, Json.toJson(task).toString, task.project_id + ":" + task.batch_id)
  }
  def addListener(taskListener: TaskListener): Boolean = {
    messageStreamListener.addMessageHandler(new MessageHandler {
      override def handle(message: String): Unit = {
        val task = Json.parse(message).validate[Task]
        task match {
          case e: JsError =>
            log.error(JsError.toJson(e).toString())
          case s: JsSuccess[Task] =>
            log.debug("Sending task {} to listener", s.get.id)
            taskListener.onTaskReceived(s.get)
        }
      }
    })
  }
}
