package example

import java.util

import io.taskmonk.entities.Notification
import org.scalatest._

class HelloSpec extends FlatSpec with Matchers {
  "The Hello object" should "say hello" in {
    val map: java.util.Map[String, String] = new util.HashMap[String, String]()
    val notification = Notification("", map)
  }
}
