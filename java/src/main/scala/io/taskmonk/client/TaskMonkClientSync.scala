package io.taskmonk.client

import java.io.File
import java.net.URL
import java.util
import java.util.concurrent.Future

import io.taskmonk.auth.Credentials
import io.taskmonk.entities._
import io.taskmonk.utils.SLF4JLogging

import scala.compat.java8.FutureConverters._
import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.duration._
import sys.process._
import java.net.URL
import java.io.File
import scala.concurrent.ExecutionContext.Implicits._



class TaskMonkClientSync(server: String, credentials: Credentials) extends SLF4JLogging  {

  var taskMonkClient =  new TaskMonkClientScala(server, credentials)

  val duration = 60 seconds


  protected def fileDownloader(url: String, filename: String) = {
    new URL(url) #> new File(filename) !!
  }

  def uploadTasksUrlSyncToBatch(projectId: String, batchId: String, fileUrl: String): TaskImportUrlResponseScala = {
    Await.result(taskMonkClient.uploadTasksUrl(projectId, batchId, fileUrl), duration)
  }

  def uploadTasksSyncToBatch(projectId: String, batchId: String, file: File): TaskImportUrlResponseScala = {
    Await.result(taskMonkClient.uploadTasks(projectId, batchId, file), duration)
  }

  def uploadTasksUrlSync(projectId: String, batchName: String, fileUrl: String): TaskImportUrlResponseScala = {
    val result = for {
      batchId <- taskMonkClient.createBatch(projectId, batchName, priority= 1, comments = None, notifications = List.empty[NotificationScala])
      importResponse <- taskMonkClient.uploadTasksUrl(projectId, batchId, fileUrl)
    } yield {
      importResponse
    }
    Await.result(result, duration)
  }

  def uploadTasksSync(projectId: String, batchName: String, file: File): TaskImportUrlResponseScala = {
    val result = for {
      batchId <- taskMonkClient.createBatch(projectId, batchName, priority= 1, comments = None, notifications = List.empty[NotificationScala])
      importResponse <- taskMonkClient.uploadTasks(projectId, batchId, file)
    } yield {
      importResponse
    }
    Await.result(result, duration)
  }


  def getJobProgressSync(projectId: String, jobId: String): JobProgressResponseScala = {
    Await.result(taskMonkClient.getJobProgress(projectId, jobId), duration)
  }

  def getBatchStatusSync(projectId: String, batchId: String): BatchSummaryScala = {
   Await.result(taskMonkClient.getBatchStatus(projectId, batchId), duration)
  }

  def createBatchSync(projectId: String, batchName: String, priority: Short, comments: String, notifications: util.List[Notification]): String  = {
    val xcomments: Option[String] = if (comments == null) {
      None
    } else {
      Some(comments)
    }

    val xnotifications: List[NotificationScala] = if (notifications == null) {
      List.empty[NotificationScala]
    } else {
      notifications.asScala.toList.map(notification =>
        NotificationScala(notification.notificationType, notification.metaData.asScala.toMap)
      )
    }

    val result = Await.result(taskMonkClient.createBatch(projectId, batchName, priority, xcomments, xnotifications), duration)
    log.info("Created batch = {}", result)
    result

  }

  def updateBatchSync(projectId: String, batchId: String, batchName: String, priority: Short, comments: String, notifications: util.List[Notification]): String  = {
    val xcomments: Option[String] = if (comments == null) {
      None
    } else {
      Some(comments)
    }

    val xnotifications: List[NotificationScala] = if (notifications == null) {
      List.empty[NotificationScala]
    } else {
      notifications.asScala.toList.map(notification =>
        NotificationScala(notification.notificationType, notification.metaData.asScala.toMap)
      )
    }

    val result = Await.result(taskMonkClient.updateBatch(projectId, batchId, batchName, priority, xcomments, xnotifications), duration)
    log.info("Updated batch = {}", result)
    result

  }

  def addTaskSync(task: Task): String = {
    val taskScala = new TaskScala(task)
    val result = Await.result(taskMonkClient.addTask(taskScala), duration)
    log.debug("Created new task {}", result)
    result
  }


  def getBatchOutputSync(projectId: String, batchId: String, outputFormat: String, outputPath: String): Boolean = {
    val result = taskMonkClient.getBatchOutput(projectId, batchId, outputFormat).map { batchOutput =>
      waitForJobCompletion(projectId, batchOutput.jobId)
      log.debug("batch job completed")
      fileDownloader(batchOutput.fileUrl, outputPath)
    }
    Await.result(result, duration)
    true

  }

  def waitForJobCompletion(projectId: String, jobId: String): Boolean = {
    var jobProgressResponseScala = getJobProgressSync(projectId, jobId)
    while (jobProgressResponseScala.percentage != 100) {
      Thread.sleep(1000)
      jobProgressResponseScala = getJobProgressSync(projectId, jobId)
    }
    true
  }

  def isUploadCompleteSync(projectId: String, batchId: String): Boolean = {
    val result = taskMonkClient.getJobProgressBatch(projectId, batchId).map { response =>
      response.completed == response.total
    }
    Await.result(result, duration)
  }

  def isProcessCompleteSync(projectId: String, batchId: String): Boolean = {
    val result = taskMonkClient.getBatchStatus(projectId, batchId).map { response =>
      response.completed == response.total
    }
    Await.result(result, duration)
  }
}
