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
        String projectId = "121";
//        String batchId = client.uploadTasks(projectId, "batchName", new File("/Users/sampath/tmp.csv")).batchId;
//        System.out.println("task batch id = " + batchId);
        String batchId = "954";
        BatchSummary batchStatus = client.getBatchStatus(projectId, batchId);
        System.out.println("Competed = " + batchStatus.completed);
        String outputPath = "/tmp/" + batchId + "_output.xlsx";
        if (batchStatus.isBatchComplete()) {
            client.getBatchOutput(projectId, batchStatus, outputPath);
        }
        System.out.println("url = " + batchStatus.fileUrl);

    }
}
