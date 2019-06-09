import io.taskmonk.auth.OAuthClientCredentials;
import io.taskmonk.client.TaskMonkClient;
import io.taskmonk.entities.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskMonkClientJava {
    public static void main(String[] args) throws InterruptedException {

        TaskMonkClient client = new TaskMonkClient("demo.taskmonk.io",
                new OAuthClientCredentials("client_id", "client_secret"));

        String projectId = "1";
        String batchId = client.uploadTasks(projectId, "batchName", new File("/tmp/tmp.csv")).batchId;
        System.out.println("task batch id = " + batchId);

        BatchSummary batchStatus = client.getBatchStatus(projectId, batchId);
        System.out.println("Competed = " + batchStatus.completed);

        String outputPath = "/tmp/" + batchId + "_output.xlsx";
        if (batchStatus.isBatchComplete()) {
            client.getBatchOutput(projectId, batchStatus, outputPath);
        }
        System.out.println("Batch output saved in " + outputPath);
    }
}
