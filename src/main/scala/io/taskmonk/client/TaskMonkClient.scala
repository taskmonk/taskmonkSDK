package io.taskmonk.client

import com.softwaremill.sttp._
import com.softwaremill.sttp.akkahttp.AkkaHttpBackend
import com.softwaremill.sttp.{HttpURLConnectionBackend, Uri}
import io.taskmonk.auth.{ApiKeyCredentials, Credentials}
import io.taskmonk.entities.{TaskImportUrlRequest, TaskImportUrlResponse}
import play.api.libs.json.{JsError, JsSuccess, Json}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import com.softwaremill.sttp.playJson._
import exceptions.ApiFailedException
import io.taskmonk.utils.SLF4JLogging

class TaskMonkClient (credentials: Credentials) extends SLF4JLogging {

  implicit val backend = AkkaHttpBackend()
  val mysttp = credentials.addAuthInfo(sttp)
  val BASE_URL = "http://localhost:9000"

  def mapResponse[T](response: Response[Either[DeserializationError[JsError], T]] ): Either[String, T] = {
    log.debug("response = {}", response)
      response.body match {
        case Left(e) =>
          Left(e)
        case Right(r) =>
          r match {
            case Left(e) =>
              Left(JsError.toJson(e.error).toString())
            case Right(r) =>
              Right(r)
          }
      }
  }

  def uploadTasks(projectId: String, taskImportUrl: TaskImportUrlRequest): Future[TaskImportUrlResponse] = {
    val url: Uri = uri"${BASE_URL}/api/project/${projectId}/import/tasks/url"
    mysttp.
      body(Json.toJson(Map("batch_name" -> "John", "fileUrl" -> "/Users/sampath/input.xls")).toString())
      .contentType("application/json")
      .post(url)
      .response(asJson[TaskImportUrlResponse])
      .send()
      .map {response =>
        mapResponse(response) match {
          case Left(e) =>
            val ex = new ApiFailedException(e)
            Future.failed(ex)
          case Right(r) =>
            Future {r}
        }
      }.flatMap(identity)
  }

}

object TaskMonkClient {
  // Set up Akka// Set up Akka

  def main(args: Array[String]): Unit = {

    /*
    implicit val backend = HttpURLConnectionBackend()

    val BASE_URL = "localhost:9000"
    val projectId = "1"
    val url: Uri = uri"${BASE_URL}/api/project/${projectId}/import/tasks/url"
    val response = sttp.
      body(Json.toJson(Map("batch_name" -> "John", "fileUrl" -> "/Users/sampath/input.xls")).toString())
      .contentType("application/json")
      .header("Authorization", api_key)
      // use an optional parameter in the URI
      .post(url)
      .response(asJson[TaskImportUrlResponse])
      .send()
    print(response)

*/
    val api_key = "M2VnQU0yNXdDRVFPS2VkQjo3ak9aWEM5Q3VaSHlGZlc0S0MxMUdvWllneXRLZ1NpaWdvd0RMYkZCbGZockZJUExsd3h1V1ZBb05FRUxqQXR0"
    val client = new TaskMonkClient(credentials = new ApiKeyCredentials(api_key))
    val fileUrl = "input.xls"
    val batchName = "batchName"
    client.uploadTasks("1", new TaskImportUrlRequest(fileUrl, batchName)).map { println(_)}
  }


}
