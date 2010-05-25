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

def run_task(detectParams, videoURL, execTgt):

    # Fixed values for HTTP post
    hostName = "10.240.33.124:8081"
    mpfTaskPath = "/LDE/mpfTask"
    mpfTaskUrl = "http://" + hostName + mpfTaskPath
    getSelector = mpfTaskPath + "/show/"
    postSelector = mpfTaskPath + "/save"
    mpfDetectorName = ("mpfDetectorName", "MppMpfDetector")
#    mpfDetectorName = ("mpfDetectorName", "MultiTesterMpfDetector")
    timeout = ("timeout", "") # No timeout
    execTgtId = ("execTgt.id", execTgt)  
    logToHost = ("log_to_host", "info")
    outToHost = ("out_to_host", "info")
    errToHost = ("err_to_host", "error")
    statusRelUrl = ("statusRelUrl", mpfTaskUrl + "/status")
    eventRelUrl = ("eventRelUrl", mpfTaskUrl + "/event")
    taskName = ("name", videoFile + "-with-" + detectParams)
    detectPrefix = ("detectPrefix", detectParams)
    videoFileUrl = ("videoFileUrl", videoURL)

    log_message("Running detector parameters " + detectParams + " against video file " + videoURL + " on exec target " + execTgt)
    postParams = taskName, mpfDetectorName, detectPrefix, videoFileUrl, timeout, execTgtId, logToHost, outToHost, errToHost, statusRelUrl, eventRelUrl
#    for param in postParams:
#        print param
    print post_multipart(hostName, postSelector, postParams, "")

#    # Wait for task to complete
#    taskId = 0 # Latest task
#    taskDone = get_task_completion(hostName, getSelector, taskId)
#    while not taskDone:
#        log_message("Task still running")
#        time.sleep(10)
#        taskDone = get_task_completion(hostName, getSelector, taskId)
#    log_message("Task complete")
    
    return True


def log_message(message):
    print datetime.datetime.now().strftime("%H:%M:%S") + ": " + message


if __name__ == '__main__':

    videoDir = "/home/appscio/videos"
    remoteVideoDir = "/mnt/"
    momentumValues = ["250", "500", "1000", "2000", "4000", "8000", "16000"]
    picThresholdValues = ["10", "20", "30", "40"]
    alphaValues = ["0.01", "0.02", "0.04", "0.08"]
#    momentumValues = ["250"]
#    picThresholdValues = ["10"]
#    alphaValues = ["0.01"]

    log_message("Starting test")

    execTgt = 3
    for videoFile in os.listdir(videoDir):
        for momentum in momentumValues:
            for alpha in alphaValues:
                for picThreshold in picThresholdValues:
                     detectParams = "momentum=%s alpha=%s picture_threshold=%s" % (momentum, alpha, picThreshold)
                     run_task(detectParams, remoteVideoDir + videoFile, str(execTgt))
        execTgt += 1

    log_message("Finished testing")
