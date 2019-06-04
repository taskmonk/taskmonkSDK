package io.taskmonk.client;

import io.taskmonk.auth.Credentials;
import io.taskmonk.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * TaskMonkClient is the basic class used for invoking REST apis with the TaskMonk
 * server
 */
public class TaskMonkClient extends TaskMonkClientSync {

  private TaskMonkClientScala taskMonkClient;

  private static final Logger log = LoggerFactory.getLogger(TaskMonkClient.class);

  /**
   * @param server - The server for accessing taskmonk. Typical values are
   *     <P>Production - www.taskmonk.io</P>
   *     <p>Demo - demo.taskmonk.io</p>
   *
   * @param credentials - Credentials is the base class for using OAuth2 to access the taskmonk apis. </br>
   * <p>The default implementation is provided by OAuthClientCredentials</p>
   */
  public TaskMonkClient(String server, Credentials credentials) {
    super(server, credentials);
    taskMonkClient = new TaskMonkClientScala(server, credentials);

  }

  /**
   * Create a new batch in the project
   * @param projectId - project in which to create the batch
   * @param batchName - name of the new batch to be created
   * @param priority - priority for the batch. Higher the number, higher the priority
   * @param comments - Any instructions that are to be displayed to the analysts
   * @param notifications - Notifications sent when batches are completed
   * @return id of the created batch
   */
  public String createBatch(String projectId, String batchName, Short priority, String comments, List<Notification> notifications) {
    return super.createBatchSync(projectId, batchName, priority, comments, notifications);
  }

  /**
   * Update an existing batch
   * @param projectId - project that the batch belongs to
   * @param batchId - batchId to update
   * @param batchName - name of the new batch to be created
   * @param priority - priority for the batch. Higher the number, higher the priority
   * @param comments - Any instructions that are to be displayed to the analysts
   * @param notifications - Notifications sent when batches are completed
   * @return id of the created batch
   */
  public String updateBatch(String projectId, String batchId, String batchName, Short priority, String comments, List<Notification> notifications) {
    return super.updateBatchSync(projectId, batchId, batchName, priority, comments, notifications);
  }

  /**
   * Upload tasks using a publicly accessible url
   * @param projectId - Id for the project
   * @param batchId - The batch to upload the tasks to
   * @param taskUrl - A publicly accessible url for the excel or csv file with the tasks to be imported
   * @return {@link io.taskmonk.entities.TaskImportResponse}
   */
  public TaskImportResponse uploadTasksUrlToBatch(String projectId, String batchId, String taskUrl) {
      TaskImportUrlResponseScala result = super.uploadTasksUrlSync(projectId, batchId, taskUrl);
      return new TaskImportResponse(result.batchId(), result.jobId());
  }

  /**
   * Upload task using a local file
   * @param projectId - project to which to upload the tasks
   * @param batchId - The batch to upload the tasks to
   * @param file - excel file that has the tasks
   * @return {@link io.taskmonk.entities.TaskImportResponse}
   */
  public TaskImportResponse uploadTasksToBatch(String projectId, String batchId, File file) {
    TaskImportUrlResponseScala result = super.uploadTasksSyncToBatch(projectId, batchId, file);
    return new TaskImportResponse(result.batchId(), result.jobId());
  }

  /**
   * Upload tasks using a publicly accessible url. The batch is created and the batch id returned to user
   * @param projectId - Id for the project
   * @param batchName - The batch to upload the tasks to
   * @param taskUrl - A publicly accessible url for the excel or csv file with the tasks to be imported
   * @return {@link io.taskmonk.entities.TaskImportResponse}
   */
  public TaskImportResponse uploadTasksUrl(String projectId, String batchName, String taskUrl) {
    TaskImportUrlResponseScala result = super.uploadTasksUrlSync(projectId, batchName, taskUrl);
    return new TaskImportResponse(result.batchId(), result.jobId());
  }

  /**
   * Upload task using a local file. The batch is created and the batch id returned to the user
   * @param projectId - project to which to upload the tasks
   * @param batchId - The batch to upload the tasks to
   * @param file - excel file that has the tasks
   * @return {@link io.taskmonk.entities.TaskImportResponse}
   */
  public TaskImportResponse uploadTasks(String projectId, String batchId, File file) {
    TaskImportUrlResponseScala result = super.uploadTasksSync(projectId, batchId, file);
    return new TaskImportResponse(result.batchId(), result.jobId());
  }



  /**
   * Get progress for an import job
   * @param projectId
   * @param jobId
   * @return
   */
  public JobProgressResponse getJobProgress(String projectId, String jobId) {
    JobProgressResponseScala result = super.getJobProgressSync(projectId, jobId);
    return new JobProgressResponse(result.completed(), result.total(), result.percentage());
  }


  /**
   * Add a new task for processing
   * @param task {@link Task}
   * @return id for the newly created class
   */
  public String addTask(Task task) {
      return super.addTaskSync(task);
  }


  /**
   * Get the status for a batch. Use this api to determine the progress on a batch
   * and if the output file is available
   * @param projectId
   * @param batchId
   * @return
   */
  public BatchSummary getBatchStatus(String projectId, String batchId) {
    return new BatchSummary(super.getBatchStatusSync(projectId, batchId));
  }
  /**
   * Get the batch output in a local file path
   * @param projectId - project id
   * @param batchStatus - batch status received from a batch status api call {@link BatchSummary}
   * @param outputPath - the path where the output file should be saved
   */
  public void getBatchOutput(String projectId, BatchSummary batchStatus, String outputPath) {
    waitForJobCompletion(projectId, batchStatus.jobId);
    fileDownloader(batchStatus.fileUrl, outputPath);
    log.debug("Saved output to {}", outputPath);
  }
}
