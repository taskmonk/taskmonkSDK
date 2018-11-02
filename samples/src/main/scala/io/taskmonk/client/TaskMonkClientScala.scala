package io.taskmonk.client

import java.io.{ByteArrayOutputStream, File}
import java.nio.file.Files
import java.util.Base64
import java.util.zip.GZIPOutputStream

import com.softwaremill.sttp.{Uri, _}
import com.softwaremill.sttp.akkahttp.AkkaHttpBackend
import com.softwaremill.sttp.playJson._
import exceptions.ApiFailedException
import io.taskmonk.auth.{ApiKeyCredentials, Credentials}
import io.taskmonk.entities._
import io.taskmonk.utils.SLF4JLogging
import play.api.libs.json.{JsError, Json}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TaskMonkClientScala(credentials: Credentials) extends SLF4JLogging {

  implicit private val backend = AkkaHttpBackend()
  private val mysttp = credentials.addAuthInfo(sttp)
  private val BASE_URL = "http://localhost:9000"

  private def mapResponse[T](response: Response[Either[DeserializationError[JsError], T]] ): Either[String, T] = {
    log.debug("code = {}; response = {}", response.code, response: Any)
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

  def uploadTasksUrl(projectId: String, taskImportUrl: TaskImportUrlRequest): Future[TaskImportUrlResponse] = {
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

  def uploadTasks(projectId: String, file: File, batchName: String): Future[TaskImportUrlResponse] = {
    uploadTasks(projectId, file, batchName, Some(1), None, List.empty[Notification])
  }
  def uploadTasks(projectId: String, file: File, batchName: String, priority: Option[Int],
                  comments: Option[String],
                  notifications: List[Notification]): Future[TaskImportUrlResponse] = {
    val bytes = Files.readAllBytes(file.toPath)
    log.debug("bytes = " + bytes.size)
    val arrOutputStream = new ByteArrayOutputStream()
    val zipOutputStream = new GZIPOutputStream(arrOutputStream)
    zipOutputStream.write(bytes)
    zipOutputStream.close()
    arrOutputStream.close()
    val output = arrOutputStream.toByteArray
    val encoded = Base64.getEncoder.encodeToString(output)

    val newBatchContent = NewBatchContent(encoded, batchName, priority, comments, notifications)


    val url: Uri = uri"${BASE_URL}/api/project/v2/${projectId}/import/tasks"
    mysttp.
      body(Json.toJson(newBatchContent))
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


  def getJobProgress(projectId: String, jobId: String): Future[JobProgressResponse] = {
    val url: Uri = uri"${BASE_URL}/api/project/${projectId}/job/${jobId}/status"
    mysttp
      .get(url)
      .response(asJson[JobProgressResponse])
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

  def getBatchStatus(projectId: String, batchId: String): Future[BatchSummaryV2] = {
    val url: Uri = uri"${BASE_URL}/api/project/v2/${projectId}/batch/${batchId}/status"
    mysttp
      .get(url)
      .response(asJson[BatchSummaryV2])
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

object TaskMonkClientScala {

  def main(args: Array[String]): Unit = {


    val api_key = "M2VnQU0yNXdDRVFPS2VkQjo3ak9aWEM5Q3VaSHlGZlc0S0MxMUdvWllneXRLZ1NpaWdvd0RMYkZCbGZockZJUExsd3h1V1ZBb05FRUxqQXR0"
    val client = new TaskMonkClientScala(credentials = new ApiKeyCredentials(api_key))
    val fileUrl = "input.xls"
    val batchName = "batchName"
    val projectId = "1"
    val file = new File("/Users/sampath/input.xls")
    val notification = Notification("Email", Map("email_address" -> "sampath06@gmail.com"))
    val newBatchContent = NewBatchContent(content = "encoded",
      batch_name = batchName, priority = Some(1), comments = Some("comments"), notifications = List(notification))
    client.uploadTasks(projectId, file, "dummy", priority = Some(1), comments = Some("comments"), notifications = List(notification)).map { importResponse =>
      println(importResponse)

      val jobId = importResponse.jobId
      client.getJobProgress(projectId, jobId).map { jobProgress =>
        println("jobProgress = {}", jobProgress)
      }.recover {
        case ex: Exception =>
          ex.printStackTrace()
      }
      client.getBatchStatus(projectId, importResponse.batchId).map { batchStatus =>
        println("batchStatus = {}", batchStatus)
      }.recover {
        case ex: Exception =>
          ex.printStackTrace()
      }
    }.recover {
      case ex: Exception =>
        ex.printStackTrace()
    }
  }


}
