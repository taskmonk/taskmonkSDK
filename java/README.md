# taskmonkclient

Client library for using taskmonk SDK

TaskMonkSDK is the java libary used for integrating with the TaskMonk tool
 
```java
import io.taskmonk.auth.ApiKeyCredentials;
import io.taskmonk.auth.Credentials;
import io.taskmonk.client.TaskMonkClient;
import io.taskmonk.entities.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskMonkClientJava {
    public static void main(String[] args) {
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
 
```

To stream tasks and result:
```java
        /*
         * Setup the task streamer
         */

        String queueName = "testqueue_fomclient";
        String accessKey = "hcZHXMnS8Do/JRuauEcUA1hj3d+EGLIOEeIwiby9uNw=";
        TaskStreamerSender sender = new TaskStreamerSender(queueName, accessKey);

        /*
         * Send a task on the stream
         */
        Map<String, String> input = new HashMap<String, String>();
        input.put("input1", "value1");
        Map<String, String> output = new HashMap<String, String>();
        Task task = new Task(UUID.randomUUID().toString(),
                projectId,
                "batchId",
        input);
        sender.send(task);

        /*
         * Consume results on the stream
         */
        String recvQueue = "testqueue_fromclient";
        String recvAccessKey = "sAu5hGbOH300Nr45jb8leGImVv+RFVmGeiV0CNqvMpE=";
        TaskStreamerListener listener = new TaskStreamerListener(recvQueue, recvAccessKey);
        listener.addListener(new TaskListener() {
                                 @Override
                                 public void onTaskReceived(Task task) {
                                     System.out.println("Recevied task {}" + task);
                                 }
                             });
 ```

Contact TaskMonk for the queue and access keys to use for the project.

## Documentation

SDK documentation is available at [docs.taskmonk.io](http://docs.tasmonk.io).


## Quickstart with gradle

Add the following repository:

```java
    maven {
        url 'https://oss.sonatype.org/content/repositories/snapshots/'
    }
}
```

Then add the following dependency


```scala
    compile 'ai.taskmonk:taskmonksdk_2.12:0.10-SNAPSHOT'
```
