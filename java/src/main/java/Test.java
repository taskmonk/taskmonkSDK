import io.taskmonk.auth.OAuthClientCredentials;
import io.taskmonk.client.TaskMonkClient;
import io.taskmonk.entities.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        TaskMonkClient client = new TaskMonkClient("localhost:9000", new OAuthClientCredentials("uIUSPlDMnH8gLEIrnlkdIPRE6bZYhHpw", "zsYgKGLUnftFgkASD8pndMwn3viA0IPoGKAiw6S7aVukgMWI8hGJflFs0P2QYxTg"));
        Short priority = 1;
        String projectId = "121";
        String tasksUrl = "https://tmpupload.blob.core.windows.net/test/tmp.xlsx";

        String batchId = client.createBatch("121", "dummy", priority, "", new ArrayList<Notification>());
        System.out.println("batchId = " + batchId);

        System.out.println(client.updateBatch(projectId, batchId, "dummy2", priority, "", new ArrayList<>()));

        TaskImportResponse taskImportResponse = client.uploadTasks(projectId, batchId, new File("/Users/sampath/tmp.xlsx"));
        System.out.println("task import job id = " + taskImportResponse.jobId);

        JobProgressResponse jobProgressResponse = client.getJobProgress(projectId, taskImportResponse.jobId);
        while (jobProgressResponse.percentage != 100) {
            System.out.println("completed percentage = " + jobProgressResponse.completed);
            Thread.sleep(1000);
            jobProgressResponse = client.getJobProgress(projectId, taskImportResponse.jobId);
        }
        System.out.println("Completed task import for batch " + batchId);

        Map<String, String> input = new HashMap<String, String>();
        input.put("Serial", "1");
        Task task = new Task("1", projectId, batchId, input);
        String taskId = client.addTask(task);
        System.out.println("Created task " + taskId);

        // Get the batch output
        String orgId = "3";
        client.getBatchOutput(orgId, projectId, batchId, "/tmp/tmp.xlsx");
        System.out.println("Got batch output to /tmp/tmp.xlsx");
    }
}
