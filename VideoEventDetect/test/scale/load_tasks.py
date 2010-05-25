#!/usr/bin/python

"""
Most of this script was copied from http://code.activestate.com/recipes/146306/
"""

import datetime, httplib, mimetypes, random

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

# Fixed values for HTTP post
hostName = "10.240.33.124:8081"
#mpfTaskUrl = "http://" + hostName + "/MpfDetectorPluginTestbed/mpfTask"
mpfTaskUrl = "http://" + hostName + "/LDE/mpfTask"
postSelector = mpfTaskUrl + "/save"
mpfDetectorName = ("mpfDetectorName", "MultiTesterMpfDetector")
#detectPrefix = ("detectPrefix", "count=16")
detectPrefix = ("detectPrefix", "count=16 int_max=1500")
videoFileUrl = ("videoFileUrl", "/NAS/NRL/EPNRL/Phase-3/activity-1.mpg")
foregroundThread = ("foregroundThread", "on")
timeout = ("timeout", "10m")
logToHost = ("log_to_host", "info")
outToHost = ("out_to_host", "info")
errToHost = ("err_to_host", "error")
statusRelUrl = ("statusRelUrl", mpfTaskUrl + "/status")
eventRelUrl = ("eventRelUrl", mpfTaskUrl + "/event")
taskTotal = 0
eventTotal = 0

print "Starting at " + datetime.datetime.now().strftime("%H:%M:%S")

for target in range(3, 7):
    for task in range(1, 21):
        taskName = ("name", "Target-" + str(target) + "-Task-" + str(task))
        print "Executing task ", taskName
        execTgtId = ("execTgt.id", str(target))  
#    eventCount = random.randint(1, 50)
        postParams = taskName, mpfDetectorName, detectPrefix, videoFileUrl, timeout, foregroundThread, execTgtId, logToHost, outToHost, errToHost, statusRelUrl, eventRelUrl
#        print "Calling post for task " + str(task) + " with " + str(eventCount) + "events"
#    for param in postParams:
#        print param
        print post_multipart(hostName, postSelector, postParams, "")
        taskTotal += 1
#        eventTotal += eventCount

print "================="
print "Created " + str(taskTotal) + " total tasks" 
print "Finished at " + datetime.datetime.now().strftime("%H:%M:%S")
