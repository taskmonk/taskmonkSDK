from taskmonksdk import taskmonkclient

api_key = "API_KEY M2VnQU0yNXdDRVFPS2VkQjo3ak9aWEM5Q3VaSHlGZlc0S0MxMUdvWllneXRLZ1NpaWdvd0RMYkZCbGZockZJUExsd3h1V1ZBb05FRUxqQXR0"
projectId = '1'
fileUplaoadTaskxls = 'resources/input.xls'
importTasksUrlFile = "https://jdlabstest.blob.core.windows.net/judemgmt/2095.14_Input.xls"

jobIds = ['208c5e3d-3cea-4787-9cd7-97acc2986344', '6be2c813-2080-4779-b23a-360d38877aaf', '31d2dac5-98d3-4f6b-b3d7-ae8cd6a3e81d']
def main():
    # dec(20, 30, 80)
    r = taskmonkclient.getProjectInfoByID(projectId, api_key)
    print(r)
    # taskmonkclient.getProjectUsers(projectId, api_key)
    # taskmonkclient.getJobProgress(projectId, '6be2c813-2080-4779-b23a-360d38877aaf', api_key)
    # taskmonkclient.uploadTasks(projectId, 'file.txt.gz', api_key)
    # taskmonkclient.getBatchStatus(projectId, '15419', api_key);
    r = taskmonkclient.importTasksUrl(projectId, importTasksUrlFile, api_key )
    # print(r)


if __name__ == "__main__":
    main()
