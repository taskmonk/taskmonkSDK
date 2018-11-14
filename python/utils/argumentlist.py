argsList = {
    "getProjectInfoByID": {
        "args": ['projectId'],
        "requiredArgs": ['projectId'],
        "type": {
            "projectId": "string"
        }
    },

    "getProjectUsers": {},
    
    "uploadTasks": {
        "args": ['projectId', 'files'],
        "requiredArgs": ['projectId', 'files'],
        "type": {
            "projectId": "string",
            "files":".gzip|.xlsx"
        }
    },

    "getJobProgress": {},

    "getBatchStatus": {},

    "importTasksUrl": {
        "args": ['projectId', 'fileUrl'],
        "requiredArgs": ['projectId', 'fileUrl'],
        "type": {
            "projectId": "string",
            "fileUrl": "http:// string"
        }
    }
}
