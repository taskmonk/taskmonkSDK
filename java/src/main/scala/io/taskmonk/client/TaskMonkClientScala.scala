package io.taskmonk.client

import java.io.{ByteArrayOutputStream, File}
import java.nio.file.Files
import java.util.Base64
import java.util.zip.GZIPOutputStream

import com.nimbusds.oauth2.sdk.AccessTokenResponse
import com.softwaremill.sttp.{Uri, _}
import com.softwaremill.sttp.akkahttp.AkkaHttpBackend
import com.softwaremill.sttp.playJson._
import exceptions.ApiFailedException
import io.taskmonk.auth._
import io.taskmonk.client.TaskMonkClientScala.MyRequest
import io.taskmonk.entities._
import io.taskmonk.utils.SLF4JLogging
import play.api.libs.json.{JsError, Json}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object TaskMonkClientScala extends SLF4JLogging {
  type MyRequest = RequestT[Empty, String, Nothing]

  def main(args: Array[String]): Unit = {


    val client = new TaskMonkClientScala("localhost:9000", credentials = new OAuthClientCredentials("clientId", "clientSecret"))
    val fileUrl = "input.xls"
    val batchName = "batchName"
    val projectId = "68"
    val file = new File("/Users/sampath/input.xls")
    val notification = NotificationScala("Email", Map("email_address" -> "sampath06@gmail.com"))
    val newBatchContent = NewBatchContent(content = "encoded",
      batch_name = batchName, priority = Some(1), comments = Some("comments"), notifications = List(notification))
    client.uploadTasks(projectId, file, "dummy", Some(1), Some("comments"), notifications = List(notification)).map { importResponse =>
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
class TaskMonkClientScala(server: String, credentials: Credentials) extends SLF4JLogging {

  implicit private val backend = AkkaHttpBackend()
  private var refreshSttp = credentials.addAuthInfo(sttp)
  private var mysttp = credentials.addAuthInfo(sttp)
  private val BASE_URL = s"http://${server}"
  private var accessTokenResponse : Option[TokenResponse] = None

  private def getSttp : Future[MyRequest] = {
    accessTokenResponse match {
      case None =>
        refreshToken()
      case Some(token) =>
        if (token.isExpired) {
          refreshToken()
        } else {
          Future {
            val result = new OAuthTokenCredentials(token).addAuthInfo(sttp)
            result
          }
        }
    }
  }
  private def refreshToken(): Future[MyRequest] = {
    val url: Uri = credentials.addAuthInfo(uri"${BASE_URL}/api/oauth2/token")
    refreshSttp.
      post(url)
      .response(asJson[TokenResponse])
      .send()
      .map {response =>
        mapResponse(response) match {
          case Left(e) =>
            val ex = new ApiFailedException(e)
            Future.failed(ex)
          case Right(r) =>
            Future {
              this.mysttp = new OAuthTokenCredentials(r).addAuthInfo(sttp)
              this.mysttp
            }
        }
      }.flatMap(identity)

  }
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
    getSttp.map { mysttp =>
      mysttp.
        body(Json.toJson(Map("batch_name" -> "John", "fileUrl" -> "/Users/sampath/input.xls")).toString())
        .contentType("application/json")
        .post(url)
        .response(asJson[TaskImportUrlResponse])
        .send()
        .map { response =>
          mapResponse(response) match {
            case Left(e) =>
              val ex = new ApiFailedException(e)
              Future.failed(ex)
            case Right(r) =>
              Future {
                r
              }
          }
        }.flatMap(identity)
    }.flatMap(identity)
  }

  def uploadTasks(projectId: String, file: File, batchName: String): Future[TaskImportUrlResponse] = {
    uploadTasks(projectId, file, batchName, Some(1), None, List.empty[NotificationScala])
  }
  def uploadTasks(projectId: String, file: File, batchName: String, priority: Option[Int],
                  comments: Option[String],
                  notifications: List[NotificationScala]): Future[TaskImportUrlResponse] = {
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
    getSttp.map { mysttp =>
      mysttp.
        body(Json.toJson(newBatchContent))
        .post(url)
        .response(asJson[TaskImportUrlResponse])
        .send()
        .map { response =>
          mapResponse(response) match {
            case Left(e) =>
              val ex = new ApiFailedException(e)
              Future.failed(ex)
            case Right(r) =>
              Future {
                r
              }
          }
        }.flatMap(identity)
    }.flatMap(identity)
  }


  def getJobProgress(projectId: String, jobId: String): Future[JobProgressResponse] = {
    log.debug("Getting job progress for job {}", jobId)
    val url: Uri = uri"${BASE_URL}/api/project/${projectId}/job/${jobId}/status"
    getSttp.map { mysttp =>

      mysttp
        .get(url)
        .response(asJson[JobProgressResponse])
        .send()
        .map { response =>
          mapResponse(response) match {
            case Left(e) =>
              val ex = new ApiFailedException(e)
              Future.failed(ex)
            case Right(r) =>
              Future {
                r
              }
          }
        }.flatMap(identity)
    }.flatMap(identity)

  }

  def getBatchStatus(projectId: String, batchId: String): Future[BatchSummaryV2] = {
    val url: Uri = uri"${BASE_URL}/api/project/v2/${projectId}/batch/${batchId}/status"
    getSttp.map { mysttp =>

      mysttp
        .get(url)
        .response(asJson[BatchSummaryV2])
        .send()
        .map { response =>
          mapResponse(response) match {
            case Left(e) =>
              val ex = new ApiFailedException(e)
              Future.failed(ex)
            case Right(r) =>
              Future {
                r
              }
          }
        }.flatMap(identity)
    }.flatMap(identity)
  }

}

