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
        String apiKey = "API_KEY";
        Credentials credentials = new ApiKeyCredentials(apiKey);
        TaskMonkClient client = new TaskMonkClient(credentials);
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
        Map<String, String> config = new HashMap<String, String>();
        config.put(Streaming.AUTH_LISTEN_STRING, "Endpoint=sb://taskmonktest3.servicebus.windows.net/;SharedAccessKeyName=Listen;SharedAccessKey=vRtYUtJ6AKli3CHu9WpelIAJjz+2qRotQcj7L6X7dTU=;EntityPath=topic1");
        config.put(Streaming.TOPIC, "topic1");
        config.put(Streaming.SUBSCIPTION_ID, "subs2");

        TaskStreamer streamer = new TaskStreamer(config);

        /*
         * Send a task on the stream
         */
        Short level = 0;
        Short status = 1;
        Task task = new Task(UUID.randomUUID().toString(),
                "",
                "",
                level,
                status);
        streamer.sendSync(task);

        /*
         * Consume results on the stream
         */
        streamer.addListener(new TaskListener() {
                                 @Override
                                 public void onTaskReceived(Task task) {
                                     System.out.println("Recevied task {}" + task);
                                 }
                             });
 ```

Contact TaskMonk for the topic and subscription id to use for the project.

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
    compile 'ai.taskmonk:taskmonksdk_2.12:0.4-SNAPSHOT'
```
