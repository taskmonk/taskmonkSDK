package io.taskmonk.integrations.azure.servicebus

import java.nio.charset.StandardCharsets

import com.microsoft.azure.servicebus._
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder
import java.time.Duration
import java.util
import java.util.concurrent.CompletableFuture

import scala.compat.java8.FutureConverters._
import scala.concurrent.ExecutionContext.Implicits._
import io.taskmonk.utils.SLF4JLogging

import scala.concurrent.Future

trait MessageHandler {
  def handle(message: String)
}
class ServiceBusSendInterface(queueName: String, accessKey: String) extends SLF4JLogging {

  val accessKeyName = "Client"

  val connectionString = s"Endpoint=sb://taskmonk.servicebus.windows.net/;SharedAccessKeyName=${accessKeyName};SharedAccessKey=${accessKey};EntityPath=${queueName}"

  def send(messageId: String, label: String, content: String): Future[_] = {
    val sendClient = new QueueClient(new ConnectionStringBuilder(connectionString, queueName), ReceiveMode.PEEKLOCK)
    val message = new Message(content)
    message.setContentType("application/json")
    message.setLabel(label)
    message.setMessageId(messageId)
    message.setTimeToLive(Duration.ofDays(14))
    log.debug("\nMessage sending: Id = {}", message.getMessageId)
    sendClient.sendAsync(message).toScala.map { x =>
      log.debug("\n\tMessage acknowledged: Id = {}", message.getMessageId)
      sendClient.close()
      x
    }


  }
}

class AzureQueueMessageHandler(messageHandler: MessageHandler) extends IMessageHandler with SLF4JLogging {

  override def notifyException(exception: Throwable, phase: ExceptionPhase): Unit = ???

  override def onMessageAsync(message: IMessage): CompletableFuture[Void] =  {
    log.debug("Message received: {}", message.getMessageId: Any)
    val body = message.getBody
    messageHandler.handle(new String(body, StandardCharsets.UTF_8))
    return CompletableFuture.completedFuture(null)
  }

}
class ServiceBusListener(queueName: String, accessKey: String) extends SLF4JLogging {
  def addMessageHandler(messageHandler: MessageHandler): Boolean = {

    import com.microsoft.azure.servicebus.QueueClient
    import com.microsoft.azure.servicebus.ReceiveMode
    import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder

    val accessKeyName = "Client"
    val connectionString = s"Endpoint=sb://taskmonk.servicebus.windows.net/;SharedAccessKeyName=${accessKeyName};SharedAccessKey=${accessKey};EntityPath=${queueName}"
    val receiveClient = new QueueClient(new ConnectionStringBuilder(connectionString, queueName), ReceiveMode.PEEKLOCK)
    val azureMessageHandler = new AzureQueueMessageHandler(messageHandler)
    receiveClient.registerMessageHandler(azureMessageHandler, new MessageHandlerOptions(1, true, Duration.ofMinutes(1)));
    true
  }


}
object ServiceBusQueueInterface extends SLF4JLogging {


  def main(args: Array[String]): Unit = {
    // Close the sender once the send operation is complete.
    val accessKeyName = "Company"

    val sharedAccessKey = "KHaZcCi4f45F+D12f/P30M495cYIy8Ai1UrlNaOASUk="
    val queueName = "testqueue"

    val sendInterface = new ServiceBusSendInterface(queueName, sharedAccessKey)
    val id = "2"
    val data = "[" + s"{'name' = '${id}', 'firstName' = 'Albert'}" + "]"
    sendInterface.send(id, "Label", data)


    val recvKey = "rJmfYPft3k0rTc51rWQPB9aB7PmwUQ6r3yrqEk50U6g="
    val recvInteface = new ServiceBusListener(queueName, recvKey)
    recvInteface.addMessageHandler(new MessageHandler {
      override def handle(message: String): Unit = {
        log.debug("Recevied emssage {}", message)
      }
    })
  }


}
