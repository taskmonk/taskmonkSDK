from utils import urlConfig, apiCall, argumentlist, utilities
import base64
import gzip
import json

argsList = argumentlist.argsList
argumentVerifier = utilities.argumentVerifier
baseURL = urlConfig.BASE_URL

class Error(Exception):
   pass

class InvalidArguments(Error):
   pass


def getProjectInfoByID(projectId=None, apiKey=None):

    try:
        if argumentVerifier([projectId, apiKey]):
            raise InvalidArguments

    except InvalidArguments:
        print('invalid arguments')
        return json.dumps(argsList['getProjectInfoByID'])

    url = baseURL + urlConfig.URLS['Project'] + '/' + projectId
    response = apiCall.get(apiKey, url, {}, 10)

    return response

def getProjectUsers(projectId, apiKey=None):
    url = baseURL + urlConfig.URLS['Project'] + '/' + projectId + '/users'
    response = apiCall.get(apiKey, url, {}, 10)
    return response

# def uploadTasks1(projectId, batchId, files=None, apiKey=None):
#     url = baseURL + urlConfig.URLS['Project'] + '/' + projectId + '/import/tasks/batch/' + batchId
#     # files = {'files': open('/Users/prashanth/Downloads/2095.14_Input.xls', 'rb')}
#     # content = "encoded", batch_name = batchName, priority = Some(priority), comments = Some(comments))
#     response = apiCall.fileUpload(apiKey, url, files , 30)
#     return response

def getJobProgress(projectId, jobId, apiKey=None):
    url = baseURL + urlConfig.URLS['Project'] + '/' +projectId + '/job/' + jobId + '/status'
    response = apiCall.get(apiKey, url, {}, 10)
    return response

def getBatchStatus(projectId, batchId, apiKey=None):
    url = baseURL + urlConfig.URLS['Project'] + '/' + projectId + '/batch/' + batchId + '/status'
    response = apiCall.get(apiKey, url, {}, 10)
    return response

def uploadTasks( projectId=None, file=None, batch_name=None, apiKey=None, priority=0, comments='', notifications={},):
    url = baseURL + urlConfig.URLS['Project'] + '/v2/' + projectId + '/import/tasks'

    try:
        if argumentVerifier([projectId, file, batch_name, apiKey]):
            raise InvalidArguments
    except InvalidArguments:
        print('invalid arguments')
        return json.dumps(argsList['uploadTasks'])

    try:
        if file.endswith('.gz'):
            fileContent = open(file, 'rb').read()
            encoded = base64.b64encode(fileContent)
        else:
            fileContent = open(file, 'rb').read()
            with gzip.open('file.txt.gz', 'wb') as f:
                f.write(fileContent)
            fileContent = open('file.txt.gz', 'rb').read()
            encoded = base64.b64encode(fileContent)

    except:
        return json.dumps({
            "response": None,
            "error": "failed to decode file, file format supported .gzip .xls .xlsx"
        })

    payload = {
      "batch_name": "batchName",
      "priority": 0,
      "comments": "string",
      "content": encoded
    }

    response = apiCall.post(apiKey, url, json.dumps(payload) , 60)

    return response


def importTasksUrl(projectId=None, fileUrl=None, apiKey=None):

    try:
        if argumentVerifier([projectId, fileUrl, apiKey]):
            raise InvalidArguments
    except InvalidArguments:
        print('invalid arguments')
        return json.dumps(argsList['importTasksUrl'])

    url = baseURL + urlConfig.URLS['Project'] + '/' + projectId + '/import/tasks/url'

    data = json.dumps({
        "fileUrl": fileUrl,
        "batch_name": "batch_name"
    })

    response = apiCall.post(apiKey, url, data , 30)

    return response
