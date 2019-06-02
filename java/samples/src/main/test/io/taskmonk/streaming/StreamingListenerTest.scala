package io.taskmonk.test.streaming


import java.util.UUID

import io.taskmonk.entities.TaskScala
import io.taskmonk.integrations.streaming.{Streaming, TaskListener, TaskStreamerScala}
import org.scalatest._
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global

class StreamingSpec extends FlatSpec with Matchers {
  "Streaming Receive" should "receive task" in {
    val config = Map("authListenString" -> "Endpoint=sb://taskmonktest3.servicebus.windows.net/;SharedAccessKeyName=Listen;SharedAccessKey=vRtYUtJ6AKli3CHu9WpelIAJjz+2qRotQcj7L6X7dTU=;EntityPath=topic1",
      "topic" -> "topic1",
      Streaming.SUBSCIPTION_ID -> "subs2")
    val taskStreamer = new TaskStreamerScala(config)
    taskStreamer.addListener(new TaskListener {
      val log = LoggerFactory.getLogger(this.getClass)
      override def onTaskReceived(task: TaskScala): Unit =
        log.debug("Recevied task {}", task)
    })
Thread.sleep(2000)
    //    val task = new Task(id = UUID.randomUUID().toString,
//      project_id = "",
//      batch_id = "",
//      status = 0,
//      next_level = 1,
//      input = None,
//      output = None,
//      unique_field_value = None,
//      error = None,
//      lastModifiedTime = None
//    )
//    taskStreamer.send(task).map { x =>
//      print(x)
//    }

  }
  "Streaming Send" should "send task" in {
    val config = Map(Streaming.AUTH_WRITE_STRING -> "Endpoint=sb://taskmonktest3.servicebus.windows.net/;SharedAccessKeyName=Send;SharedAccessKey=SBLT2hzRISyltA/ghvoaQVI3mVfdj2jh59JJp+Z2a+c=;EntityPath=topic1",
      Streaming.TOPIC -> "topic1")
    val taskStreamer = new TaskStreamerScala(config)
        val task = new TaskScala(id = UUID.randomUUID().toString,
          project_id = "",
          batch_id = "",
          status = 0,
          next_level = 1,
          input = None,
          output = None,
          unique_field_value = None,
          error = None,
          lastModifiedTime = None
        )
        taskStreamer.send(task).map { x =>
          print(x)
        }

  }
}
