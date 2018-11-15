# taskmonkclient

Python client library for using taskmonk SDK

TaskMonkSDK is the python libary used for integrating with the TaskMonk tool
 
```python
from taskmonksdk.taskmonkclient import TaskMonkSDK

api_key = "M2VnQU0yNXdDRVFPS2VkQjo3ak9aWEM5Q3VaSHlGZlc0S0MxMUdvWllneXRLZ1NpaWdvd0RMYkZCbGZockZJUExsd3h1V1ZBb05FRUxqQXR0"
projectId = '53'
tasksInput = 'resources/input.xls'
importTasksUrlFile = "https://jdlabstest.blob.core.windows.net/judemgmt/2095.14_Input.xls"

jobIds = ['208c5e3d-3cea-4787-9cd7-97acc2986344', '6be2c813-2080-4779-b23a-360d38877aaf', '31d2dac5-98d3-4f6b-b3d7-ae8cd6a3e81d']
def main():
    # dec(20, 30, 80)
    client = TaskMonkSDK("http://localhost:9000", api_key)
    r = client.getProjectInfoByID(projectId)
    print(r.maximum_fields)
    notification_email = "info@taskmonk.ai"
    r = client.uploadTasks(projectId=projectId, file=tasksInput, batch_name="batchName", notification_email = notification_email)
    print('upload response = ', r)
    print('batch_id = %s' % (r.batchId))
    job_id = r.jobId
    print('job_id = %s' % job_id)
    r = client.getJobProgress(projectId, job_id)
    print('job_progress = ', r)


if __name__ == "__main__":
    main()

```

## Documentation

SDK documentation is available at [example.com](http://example.com).


## Quickstart 


```shell
# Clone the github repository https://github.com/taskmonk/taskmonkSDK
# cd python
# sudo pip install .
}
```