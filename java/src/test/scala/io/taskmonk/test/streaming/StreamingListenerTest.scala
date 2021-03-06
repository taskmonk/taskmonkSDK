package io.taskmonk.test.streaming


import java.util
import java.util.UUID

import io.taskmonk.entities.TaskScala
import io.taskmonk.integrations.streaming._
import org.scalatest._
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global

class StreamingSpec extends FlatSpec with Matchers {
  "Streaming Send" should "send task" in {

    val taskStreamer = new TaskStreamerSender("testqueue_fromclient", "hcZHXMnS8Do/JRuauEcUA1hj3d+EGLIOEeIwiby9uNw=")
    val input: java.util.Map[String, String] = new util.HashMap[String, String]
    input.put("input1", "value1")
    val task = new TaskScala(externalId = UUID.randomUUID().toString,
      project_id = "68",
      batch_id = "230",
      input = input)

    taskStreamer.send(task).map { x =>
      print(x)
    }
    Thread.sleep(1000);
  }
  "Streaming Receive" should "receive task" in {
    val taskStreamer = new TaskStreamerListener("testqueue_toclient", "sAu5hGbOH300Nr45jb8leGImVv+RFVmGeiV0CNqvMpE=")
    taskStreamer.addListener(new TaskListener {
      val log = LoggerFactory.getLogger(this.getClass)
      override def onTaskReceived(task: TaskScala): Unit =
        log.debug("Recevied task {}", task)
    })
    Thread.sleep(100000);
  }

}
