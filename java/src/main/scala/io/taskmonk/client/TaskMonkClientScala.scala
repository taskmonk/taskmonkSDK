package io.taskmonk.client

import io.taskmonk.entities._
import java.io.{ByteArrayOutputStream, File}
import java.nio.file.Files
import java.util
import java.util.{Base64}
import java.util.zip.GZIPOutputStream

import com.nimbusds.oauth2.sdk.AccessTokenResponse
import com.softwaremill.sttp.{Uri, _}
import com.softwaremill.sttp.akkahttp.AkkaHttpBackend
import com.softwaremill.sttp.playJson._
import exceptions.ApiFailedException
import io.taskmonk.auth._
import io.taskmonk.client.TaskMonkClientScala.MyRequest
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
//    client.uploadTasks(projectId, file, "dummy", Some(1), Some("comments"), notifications = List(notification)).map { importResponse =>
//      println(importResponse)
//      val jobId = importResponse.jobId
//      client.getJobProgress(projectId, jobId).map { jobProgress =>
//        println("jobProgress = {}", jobProgress)
//      }.recover {
//        case ex: Exception =>
//          ex.printStackTrace()
//      }
//      client.getBatchStatus(projectId, importResponse.batchId).map { batchStatus =>
//        println("batchStatus = {}", batchStatus)
//      }.recover {
//        case ex: Exception =>
//          ex.printStackTrace()
//      }
//    }.recover {
//      case ex: Exception =>
//        ex.printStackTrace()
//    }
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

  def uploadTasksUrl(projectId: String, batchId: String, tasksUrl: String): Future[TaskImportUrlResponseScala] = {
    val url: Uri = uri"${BASE_URL}/api/project/${projectId}/batch/${batchId}/tasks/import/url"
    getSttp.map { mysttp =>
      mysttp.
        body(Map("file_url" -> tasksUrl))
        .post(url)
        .response(asJson[TaskImportUrlResponseScala])
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

  def uploadTasks(projectId: String, batchId: String, file: File): Future[TaskImportUrlResponseScala] = {
    log.debug("Uploading tasks to batch {}", batchId)
    val reg_ex = """.*\.(\w+)""".r
    val fileType = file.getAbsolutePath match {
      case reg_ex(ext) => ext
      case _ => ".xlsx"
    }
    log.debug("fileType = {}", fileType)
    val bytes = Files.readAllBytes(file.toPath)
    val arrOutputStream = new ByteArrayOutputStream()
    val zipOutputStream = new GZIPOutputStream(arrOutputStream)
    zipOutputStream.write(bytes)
    zipOutputStream.close()
    arrOutputStream.close()
    val output = arrOutputStream.toByteArray
    val encoded = Base64.getEncoder.encodeToString(output)

    val url: Uri = uri"${BASE_URL}/api/project/${projectId}/batch/${batchId}/tasks/import?fileType=${fileType}"
    log.debug("Upload url = {}", url)
    getSttp.map { mysttp =>
      mysttp.
        body(encoded)
        .contentType("text/plain", "gzip")
        .post(url)
        .response(asJson[TaskImportUrlResponseScala])
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


  def getJobProgress(projectId: String, jobId: String): Future[JobProgressResponseScala] = {
    log.debug("Getting job progress for job {}", jobId)
    val url: Uri = uri"${BASE_URL}/api/project/${projectId}/job/${jobId}/status"
    getSttp.map { mysttp =>

      mysttp
        .get(url)
        .response(asJson[JobProgressResponseScala])
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

  def getJobProgressBatch(projectId: String, batchId: String): Future[JobProgressResponseScala] = {
    log.debug("Getting job progress for batch {}", batchId)
    val url: Uri = uri"${BASE_URL}/api/project/${projectId}/job/${batchId}/status?input_type=batch"
    log.debug("progress url = {}", url)
    getSttp.map { mysttp =>

      mysttp
        .get(url)
        .response(asJson[JobProgressResponseScala])
        .send()
        .map { response =>
          mapResponse(response) match {
            case Left(e) =>
              val ex = new ApiFailedException(e)
              Future.failed(ex)
            case Right(r) =>
              Future {
                log.debug("r = {}", r)
                r
              }
          }
        }.flatMap(identity)
    }.flatMap(identity)

  }

  def getBatchStatus(projectId: String, batchId: String): Future[BatchSummaryScala] = {
    val url: Uri = uri"${BASE_URL}/api/project/v2/${projectId}/batch/${batchId}/status"
    log.debug("status url = {}", url)
    getSttp.map { mysttp =>

      mysttp
        .get(url)
        .response(asJson[BatchSummaryScala])
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

  def getBatchOutput(projectId: String, batchId: String, outputFormat: String): Future[BatchOutput] = {
    val url: Uri = uri"${BASE_URL}/api/project/${projectId}/batch/${batchId}/output?output_format=${outputFormat}"
    log.debug("get batch output url = {}", url)
    val outputFields = Seq.empty[String]
    getSttp.map { mysttp =>
      mysttp
        .body(Json.toJson(Map("fieldNames" -> outputFields)))
        .post(url)
        .contentType("application/json")
        .response(asJson[BatchOutput])
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

  def createBatch(projectId: String, batchName: String, priority: Short, comments: Option[String], notifications: List[NotificationScala]): Future[String] = {
    val url: Uri = uri"${BASE_URL}/api/project/${projectId}/batch"
    log.debug("Sending post to {}", url)
    getSttp.map { mysttp =>
      val newBatchData = NewBatchData(batchName, Some(priority), comments, notifications)
      mysttp
          .body(Json.toJson(newBatchData))
          .post(url)
        .response(asString)
          .send()
          .map { response =>
            log.info("create batch response = {}", response)
            response.body match {
              case Left(e) =>
                val ex = new ApiFailedException(e)
                Future.failed(ex)
              case Right(r) =>
                Future {
                  val json = Json.parse(r)
                  val result = (json \ "id").validate[String].get
                  result
                }
            }
           }.flatMap(identity)
    }.flatMap(identity)
  }

  def updateBatch(projectId: String, batchId: String, batchName: String, priority: Short, comments: Option[String], notifications: List[NotificationScala]): Future[String] = {
    val url: Uri = uri"${BASE_URL}/api/project/${projectId}/batch/${batchId}"
    log.debug("Sending post to {}", url)
    getSttp.map { mysttp =>
      val newBatchData = NewBatchData(batchName, Some(priority), comments, notifications)
      mysttp
        .body(Json.toJson(newBatchData))
        .put(url)
        .response(asString)
        .send()
        .map { response =>
          log.info("create batch response = {}", response)
          response.body match {
            case Left(e) =>
              val ex = new ApiFailedException(e)
              Future.failed(ex)
            case Right(r) =>
              Future {
                val json = Json.parse(r)
                val result = (json \ "id").validate[String].get
                result
              }
          }
        }.flatMap(identity)
    }.flatMap(identity)
  }


  def addTask(taskScala: TaskScala): Future[String] = {
    val url: Uri = uri"${BASE_URL}/api/project/${taskScala.project_id}/task/external"
    log.debug("Sending post to {}", url)
    getSttp.map { mysttp =>
      mysttp
        .body(Json.toJson(taskScala))
        .post(url)
        .response(asString)
        .send()
        .map { response =>
          log.info("create task response = {}", response)
          response.body match {
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

