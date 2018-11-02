package io.taskmonk.client

import java.io.File
import java.util.concurrent.{CompletionStage, Future}

import io.taskmonk.auth.Credentials
import io.taskmonk.entities._

import scala.compat.java8.FutureConverters._
import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.duration._

class TaskMonkClient(credentials: Credentials) {

  var taskMonkClient =  new TaskMonkClientScala(credentials)

  val duration = 60 seconds

  def uploadTasksUrl(projectId: String, taskImportUrl: TaskImportUrlRequest): TaskImportUrlResponse = {
    Await.result(taskMonkClient.uploadTasksUrl(projectId, taskImportUrl), duration)
  }

  def uploadTasks(projectId: String, file: File, batchName: String): TaskImportUrlResponse = {
    Await.result(taskMonkClient.uploadTasks(projectId, file, batchName), duration)
  }

  def uploadTasks(projectId: String, file: File, batchName: String, priority: Int,
                  comments: String,
                  notifications: java.util.List[Notification]): TaskImportUrlResponse = {
    val xpriority: Option[Int] = if (priority == null) {
      None
    } else {
      Some(priority)
    }

    val xcomments: Option[String] = if (comments == null) {
      None
    } else {
      Some(comments)
    }

    val xnotifications: List[Notification] = if (notifications == null) {
      List.empty[Notification]
    } else {
      notifications.asScala.toList
    }

    Await.result(taskMonkClient.uploadTasks(projectId, file, batchName, xpriority, xcomments, xnotifications), duration)
  }

  def getJobProgress(projectId: String, jobId: String): JobProgressResponse = {
    Await.result(taskMonkClient.getJobProgress(projectId, jobId), duration)
  }

  def getBatchStatus(projectId: String, batchId: String): BatchSummaryV2 = {
    Await.result(taskMonkClient.getBatchStatus(projectId, batchId), duration)

  }




  }
