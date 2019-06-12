import io.taskmonk.auth.OAuthClientCredentials;
import io.taskmonk.client.TaskMonkClient;

import java.io.File;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        String projectId = "169";
        /**
         * Initialize the taskmonk client witht he oauth credentials and projectId
         */
        TaskMonkClient client = new TaskMonkClient(projectId, "preprod.taskmonk.io",
                new OAuthClientCredentials("uIUSPlDMnH8gLEIrnlkdIPRE6bZYhHpw", "zsYgKGLUnftFgkASD8pndMwn3viA0IPoGKAiw6S7aVukgMWI8hGJflFs0P2QYxTg"));
        String batchId = "331";

        /*
         * Wait while the tasks are being processed by the analysts
         */
        while (!client.isProcessComplete(batchId)) {
            System.out.println("Processing not complete");
            Thread.sleep(1000);
        }
        System.out.println("Upload Completed");

        /*
         * Get the output in a local csv file
         */
        String outputPath = client.getBatchOutput(batchId);
        System.out.println("outputPath = " + outputPath);
    }
}
