import json
import requests
import sys

def get(apiKey, url='', data={}, timeout=10):
    headers = {
        'Content-Type': 'application/json',
        'Authorization': apiKey
    }

    try:
        print(url)
        r = requests.get(url, timeout=timeout, headers=headers)
        r.raise_for_status()
        resp = r.json()
        return json.dumps({
            "response": resp,
            "error": None
        })

    except Exception as e:
        return json.dumps({
            "error": str(e),
            "response": None
        })

def post(apiKey, url='', data={}, timeout=30):
    headers = {
        'Content-Type': 'application/json',
        'Authorization': apiKey,
    }

    try:
        print(url, data)
        r = requests.post(url,  headers=headers, data=data, timeout=timeout)
        r.raise_for_status()
        resp = r.json()
        return json.dumps({
            "response": resp,
            "error": None
        })

    except Exception as e:
        return json.dumps({
            "error": str(e),
            "response": None
        })


def fileUpload(apiKey, url='', files={}, timeout=30):
    headers = {
        'Content-Type': 'application/json',
        'Authorization': apiKey
    }
    
    try:
        print(url, files)
        r = requests.post(url, files=files, timeout=timeout, headers=headers)
        r.raise_for_status()
        resp = r.json()
        return json.dumps({
            "response": resp,
            "error": None
        })

    except Exception as e:
        return json.dumps({
            "error": str(e),
            "response": None
        })
    # except requests.exceptions.ConnectionError as e:
    #     return {
    #         "error": e,
    #         "response": None
    #     }
    #
    # except requests.exceptions.Timeout as e:
    #     return {
    #         "error": e,
    #         "response": None
    #     }
    #
    # except requests.exceptions.TooManyRedirects as e:
    #     return {
    #         "error": e,
    #         "response": None
    #     }
    #
    # except requests.exceptions.HTTPError as e:
    #     return {
    #         "error": e,
    #         "response": None
    #     }
    #
    # except requests.exceptions.RequestException as e:
    #     return {
    #         "error": e,
    #         "response": None
    #     }
