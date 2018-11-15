from utils import urlConfig, apiCall, argumentlist, utilities
import base64
import gzip
import json

argsList = argumentlist.argsList
argumentVerifier = utilities.argumentVerifier

class Error(Exception):
   pass

class InvalidArguments(Error):
   pass


class TaskMonkSDK:
    baseURL = urlConfig.BASE_URL
    apiKey = None 
    def __init__(self, base_url=urlConfig.BASE_URL, api_key= None):
        self.baseURL = base_url
        self.apiKey = api_key

    def getProjectInfoByID(self, projectId=None):
    
        try:
            if argumentVerifier([projectId, self.apiKey]):
                raise InvalidArguments
    
        except InvalidArguments:
            print('invalid arguments')
            return json.dumps(argsList['getProjectInfoByID'])
    
        url = self.baseURL + urlConfig.URLS['Project'] + '/' + projectId
        response = apiCall.get(self.apiKey, url, {}, 10)
    
        return response
    
    def getProjectUsers(self, projectId):
        url = self.baseURL + urlConfig.URLS['Project'] + '/' + projectId + '/users'
        response = apiCall.get(self.apiKey, url, {}, 10)
        return response
    
    # def uploadTasks1(projectId, batchId, files=None, self.apiKey=None):
    #     url = self.baseURL + urlConfig.URLS['Project'] + '/' + projectId + '/import/tasks/batch/' + batchId
    #     # files = {'files': open('/Users/prashanth/Downloads/2095.14_Input.xls', 'rb')}
    #     # content = "encoded", batch_name = batchName, priority = Some(priority), comments = Some(comments))
    #     response = apiCall.fileUpload(self.apiKey, url, files , 30)
    #     return response
    
    def getJobProgress(self, projectId, jobId):
        url = self.baseURL + urlConfig.URLS['Project'] + '/' +projectId + '/job/' + jobId + '/status'
        response = apiCall.get(self.apiKey, url, {}, 10)
        return response
    
    def getBatchStatus(self, projectId, batchId):
        url = self.baseURL + urlConfig.URLS['Project'] + '/' + projectId + '/batch/' + batchId + '/status'
        response = apiCall.get(self.apiKey, url, {}, 10)
        return response
    
    def uploadTasks(self,  projectId=None, file=None, batch_name=None, priority=0, comments='', notifications={},):
        url = self.baseURL + urlConfig.URLS['Project'] + '/v2/' + projectId + '/import/tasks'
    
        try:
            if argumentVerifier([projectId, file, batch_name, self.apiKey]):
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
    
        response = apiCall.post(self.apiKey, url, json.dumps(payload) , 60)
    
        return response
    
    
    def importTasksUrl(self, projectId=None, fileUrl=None):
    
        try:
            if argumentVerifier([projectId, fileUrl, self.apiKey]):
                raise InvalidArguments
        except InvalidArguments:
            print('invalid arguments')
            return json.dumps(argsList['importTasksUrl'])
    
        url = self.baseURL + urlConfig.URLS['Project'] + '/' + projectId + '/import/tasks/url'
    
        data = json.dumps({
            "fileUrl": fileUrl,
            "batch_name": "batch_name"
        })
    
        response = apiCall.post(self.apiKey, url, data , 30)
    
        return response
