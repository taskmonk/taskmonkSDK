package io.taskmonk.test.streaming


import io.taskmonk.integrations.streaming.TaskStreamer
import org.scalatest._

class StreamingSpec extends FlatSpec with Matchers {
  "Streaming Receive" should "receive task" in {
    val config = Map("authListenString" -> "", "topic" ->)
    val taskStreamer = new TaskStreamer(config)

  }
}
