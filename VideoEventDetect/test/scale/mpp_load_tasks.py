#!/usr/bin/python

"""
Most of this script was copied from http://code.activestate.com/recipes/146306/
"""

import datetime, httplib, mimetypes, os, time

def post_multipart(host, selector, fields, files):
    """
    Post fields and files to an http host as multipart/form-data.
    fields is a sequence of (name, value) elements for regular form fields.
    files is a sequence of (name, filename, value) elements for data to be uploaded as files
    Return the server's response page.
    """
    content_type, body = encode_multipart_formdata(fields, files)
    h = httplib.HTTP(host)
    h.putrequest('POST', selector)
    h.putheader('content-type', content_type)
    h.putheader('content-length', str(len(body)))
    h.endheaders()
    h.send(body)
    errcode, errmsg, headers = h.getreply()
#    print "Response code = " + str(errcode)
#    print "Response message = " + errmsg
#    print "Response headers:" 
#    print headers
#    print "Got HTTP response " + h.file.read()
    return h.file.read()


def encode_multipart_formdata(fields, files):
    """
    fields is a sequence of (name, value) elements for regular form fields.
    files is a sequence of (name, filename, value) elements for data to be uploaded as files
    Return (content_type, body) ready for httplib.HTTP instance
    """
    BOUNDARY = '----------ThIs_Is_tHe_bouNdaRY_$'
    CRLF = '\r\n'
    L = []
    for (key, value) in fields:
        L.append('--' + BOUNDARY)
        L.append('Content-Disposition: form-data; name="%s"' % key)
        L.append('')
        L.append(value)
    for (key, filename, value) in files:
        L.append('--' + BOUNDARY)
        L.append('Content-Disposition: form-data; name="%s"; filename="%s"' % (key, filename))
        L.append('Content-Type: %s' % get_content_type(filename))
        L.append('')
        L.append(value)
    L.append('--' + BOUNDARY + '--')
    L.append('')
    body = CRLF.join(L)
    content_type = 'multipart/form-data; boundary=%s' % BOUNDARY
    return content_type, body


def get_content_type(filename):
    return mimetypes.guess_type(filename)[0] or 'application/octet-stream'


def get_http(host, selector):
    """
    Get a URL from an HTTP host
    Return the raw HTTP response.
    """
    h = httplib.HTTP(host)
    h.putrequest('GET', selector)
    h.putheader('Accept', 'text/html')
    h.putheader('Accept', 'text/plain')
    h.endheaders()

#    print "Doing HTTP GET against http://" + host + selector

    errcode, errmsg, headers = h.getreply()
#    print "Got return code " + str(errcode) # Should be 200
    data = h.getfile().read() # Get the raw HTML
    return data


def get_task_completion(host, getSelector, taskId):
    selector = getSelector + str(taskId)
    data = get_http(host, selector).strip()

    if data.find("SUCCEEDED") > 0:
        return True
    elif data.find("FAILED") > 0:
        return True

    return False


# Fixed values for HTTP post
hostName = "127.0.0.1:8080"
mpfTaskPath = "/MpfDetectorPluginTestbed/mpfTask"
mpfTaskUrl = "http://" + hostName + mpfTaskPath
getSelector = mpfTaskPath + "/show/"
postSelector = mpfTaskPath + "/save"
mpfDetectorName = ("mpfDetectorName", "MppMpfDetector")
foregroundThread = ("foregroundThread", "on")
timeout = ("timeout", "") # No timeout
execTgtId = ("execTgt.id", "1")  
logToHost = ("log_to_host", "info")
outToHost = ("out_to_host", "info")
errToHost = ("err_to_host", "error")
statusRelUrl = ("statusRelUrl", mpfTaskUrl + "/status")
eventRelUrl = ("eventRelUrl", mpfTaskUrl + "/event")
taskId = 2

# List of detector scripts to use for each video file
detectorScripts = ("detect-md.sh", "detect-usc.sh", "detect-md-usc.sh", "detect-md-clips.sh")
videoDir = "/NAS/NRL/MPP-test/"

print datetime.datetime.now().strftime("%H:%M:%S") + ": Starting test"
for videoFile in os.listdir(videoDir):
    for detector in detectorScripts:
#        detector = "detect-md.sh"
        print datetime.datetime.now().strftime("%H:%M:%S") + ": Running detector " + detector + " against video file " + videoFile
        taskName = ("name", videoFile + "-with-" + detector)
        detectPrefix = ("detectPrefix", "detector=" + detector)
        videoFileUrl = ("videoFileUrl", videoDir + videoFile)
        postParams = taskName, mpfDetectorName, detectPrefix, videoFileUrl, timeout, foregroundThread, execTgtId, logToHost, outToHost, errToHost, statusRelUrl, eventRelUrl
#        for param in postParams:
#            print param
        print post_multipart(hostName, postSelector, postParams, "")

        # Wait for task to complete
        taskDone = get_task_completion(hostName, getSelector, taskId)
        while not taskDone:
            print datetime.datetime.now().strftime("%H:%M:%S") + ": Task still running"
            time.sleep(5)
            taskDone = get_task_completion(hostName, getSelector, taskId)
        print datetime.datetime.now().strftime("%H:%M:%S") + ": Task complete"
    taskId += 1
print datetime.datetime.now().strftime("%H:%M:%S") + ": Finished"
