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
        Credentials credentials = new OAuthClientCredentials("clientId", "clientSecret");
        String server = "http://demo.taskmonk.io";
        TaskMonkClient client = new TaskMonkClient(server, credentials);
        String projectId = "1";
        String batchName = "batchName";
        String fileUrl = "http://blob.example.azure.com/filepath";

        // Upload from url
        TaskImportUrlRequest request = new TaskImportUrlRequest(fileUrl, batchName);
        TaskImportUrlResponse response = client.uploadTasksUrl(projectId, request);

        JobProgressResponse jobProgressResponse = client.getJobProgress(projectId, response.jobId());


        // Upload from file
        File file = new File("localpath");

        // Set priority; Higher the number higher the priority
        Integer priority = 1;
        String comments = "Special Directions to BPO";
        Map<String, String> metaData = new HashMap<String, String>();
        metaData.put("email_address", "info@taskmonk.ai");
        Notification notification = new Notification("email", metaData);

        List<Notification> notifications = new ArrayList<Notification>();
        notifications.add(notification);

        response = client.uploadTasks(projectId, file, batchName, priority, comments, notifications);
        System.out.println("Uploaded tasks; jobId = " + response.jobId());


    }
}       
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

SDK documentation is available at [example.com](http://example.com).


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
    compile 'ai.taskmonk:taskmonksdk_2.12:0.8-SNAPSHOT'
```
