package io.taskmonk.integrations.azure.servicebus

import java.time.Duration
import java.util.concurrent.CompletableFuture

import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder
import com.microsoft.azure.servicebus._

import scala.compat.java8.FutureConverters._
import play.api.libs.json.Json
import java.nio.charset.StandardCharsets

import io.taskmonk.integrations.streaming.Streaming
import io.taskmonk.utils.SLF4JLogging
import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._



trait MessageHandler {
  def handle(message: String)
}
class AzureMessageHandler(subscriptionClient: SubscriptionClient, messageHandler: MessageHandler) extends IMessageHandler with SLF4JLogging {
  override def notifyException(exception: Throwable, phase: ExceptionPhase): Unit = ???

  override def onMessageAsync(message: IMessage): CompletableFuture[Void] =  {
    log.debug("{} Message received: {}", subscriptionClient.getEntityPath, message.getMessageId: Any)
    val body = message.getBody
    messageHandler.handle(new String(body, StandardCharsets.UTF_8))
    return subscriptionClient.completeAsync(message.getLockToken)
  }

}

class ServiceBusSendInterface(configuration: Map[String, String]) extends SLF4JLogging {
  val primConnString = configuration.get(Streaming.AUTH_WRITE_STRING)
  val topicName = configuration.get(Streaming.TOPIC)

  var topicClient: Option[TopicClient] = if (primConnString.isDefined && topicName.isDefined) {
      Some(new TopicClient(new ConnectionStringBuilder(primConnString.get, topicName.get)))
  } else {
    None
  }

  def send(messageId: String, content: String, label: String): Future[_] = {
    topicClient match {
      case None =>
        val ex = new InternalError("Topic Client not initialised")
        Future.failed(ex)
      case Some(client) =>
        val message = new Message(content)
        message.setContentType("application/json")
        message.setLabel(label)
        message.setMessageId(messageId)
        message.setTimeToLive(Duration.ofDays(14))
        log.debug("\nMessage sending: Id = {}", message.getMessageId)
        client.sendAsync(message).toScala.map { x =>
          log.debug("\n\tMessage acknowledged: Id = {}", message.getMessageId)
          x
        }
    }
  }
}

class ServiceBusListener(configuration: Map[String, String]) extends SLF4JLogging {

  val listenConnString = configuration.getOrElse(Streaming.AUTH_LISTEN_STRING, "")
  var subscription1Client : Option[SubscriptionClient] = None
  if (listenConnString.isEmpty) {
    log.error("Failed to get listen connection string")
  } else {

    val topicName = configuration.getOrElse(Streaming.TOPIC, "")
    val subscriptionId = configuration.getOrElse(Streaming.SUBSCIPTION_ID, "")

    val subscriptionName = topicName + "/Subscriptions/" + subscriptionId
    subscription1Client = Some(new SubscriptionClient(new ConnectionStringBuilder(listenConnString, subscriptionName), ReceiveMode.PEEKLOCK))

  }
  def addMessageHandler(messageHandler: MessageHandler): Boolean = {
    subscription1Client.map { x =>
      val azureMessageHandler = new AzureMessageHandler(x, messageHandler)
      x.registerMessageHandler(azureMessageHandler)
      return true
    }
    return true;

  }

}