#!/usr/bin/python

"""
Most of this script was copied from http://code.activestate.com/recipes/146306/
"""

import datetime, httplib, mimetypes, os, pexpect, time
from boto.ec2.connection import EC2Connection

EC2_ACCOUNT = "227791630297"
EC2_HOME ="/root/ec2-tools"
EC2_SECRET_KEY ="PkcB9hqkJy1TJITTPJ8sdtlQssYtUtBze8X3qH8l"
EC2_ACCESS_KEY ="AKIAIJ3B4W6TLAOESPHA"
EC2_PRIVATE_KEY = "/root/svn-checkouts/company/fox-ec2/pk-GS6EIF7MHM7S3YW43IMMT52Q77O7US4A.pem"
EC2_CERT = "/root/svn-checkouts/company/fox-ec2/cert-GS6EIF7MHM7S3YW43IMMT52Q77O7US4A.pem"

# EC2 instance launch data
DEFAULT_EC2_AMI = "ami-7b24ca12"  # 64-bit Fedora 11 image with MPF detector dependencies
DEFAULT_EC2_INSTANCE_TYPE = "m2.xlarge"   # Fastest instance type for MPF detectors
DEFAULT_EC2_KEY = "/root/svn-checkouts/company/fox-ec2/id_rsa-fox-keypair"
DEFAULT_EC2_GROUPS = ["fox-phase-1"]
SSH_ROOT_OPTS = "-o StrictHostKeyChecking=no -i %s" % DEFAULT_EC2_KEY 
SSH_USER_OPTS = "-o StrictHostKeyChecking=no" 


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


def get_new_exec_tgt_id(host, getSelector):
    selector = getSelector + "0"
    data = get_http(host, selector).strip()

    newTgtId = data.split("class=\"name\">ID:</td>\n\n                            <td valign=\"top\" class=\"value\">")[-1].split("</td>")[0]         # This is starting to look too much like Perl :( 
    return newTgtId


def get_new_task_id(host, getSelector):
    selector = getSelector + "0"
    data = get_http(host, selector).strip()

    newTaskId = data.split("class=\"name\">ID:</td>\n\n                            <td valign=\"top\" class=\"value\">")[-1].split("</td>")[0]         # This is starting to look too much like Perl :( 
    return newTaskId


def prepare_ec2_exec_targets(tgt_user, num_tgts, instance_type):
    """
    Launch EC2 instances to use as mpfExecTgts
    """
    conn = EC2Connection(EC2_ACCESS_KEY, EC2_SECRET_KEY)
    reservations = conn.get_all_instances(["i-95146afe", "i-6f156b04", "i-cf156ba4", "i-850977ee"])
#    reservations = conn.run_instances(DEFAULT_EC2_AMI, 
#                                     min_count=num_tgts,
#                                     max_count=num_tgts,
#                                     instance_type=instance_type,
#                                     key_name=DEFAULT_EC2_KEY,
#                                     security_groups=DEFAULT_EC2_GROUPS)
    taskCount = 0
    try:
        for res in reservations:
            instance = res.instances[0]
            print instance

            while not instance.update() == 'running':
                time.sleep(10)
                print "waiting for instance to start up ..."

            print "Started the instance: %s" % instance.dns_name

            print "Copying video files to target instance ..." 
            os.system( "scp %s /home/appscio/videos/* root@%s:/mnt" % 
	    				(SSH_ROOT_OPTS, instance.dns_name))
		
            print "Installing updates ..." 
            os.system("ssh %s root@%s \"yum install -y gstreamer-plugins-flumpegdemux\"" % 
					(SSH_ROOT_OPTS, instance.dns_name))
            os.system("ssh %s root@%s \"yum update -y\"" % 
					(SSH_ROOT_OPTS, instance.dns_name))

    except Exception, e:
       print e

#    instance.terminate()
#    while not instance.update() == 'terminated':
#        time.sleep(10)
#        print "waiting for instance to terminate ..."

#    print "Stopped the instance: %s" % instance.dns_name

    return reservations


if __name__ == '__main__':

    # Fixed values for HTTP post
    hostName = "10.240.33.124:8081"
#    mpfExecTgtPath = "/MpfDetectorPluginTestbed/mpfExecTgt"
#    mpfTaskPath = "/MpfDetectorPluginTestbed/mpfTask"
    mpfExecTgtPath = "/LDE/mpfExecTgt"
    mpfTaskPath = "/LDE/mpfTask"
    mpfTaskUrl = "http://" + hostName + mpfExecTgtPath
    mpfTaskUrl = "http://" + hostName + mpfTaskPath
    taskGetSelector = mpfTaskPath + "/show/"
    execTgtGetSelector = mpfExecTgtPath + "/show/"
    execTgtPostSelector = mpfExecTgtPath + "/save"
    taskPostSelector = mpfTaskPath + "/save"

    execTgtUser = ("username", "mpf1")
    execTgtPassword = ("pw", "mpf1mpf1")
    execTgtLimit = ("limitTasks", "2")
    execTgtPriority = ("priority", "0")

    mpfDetectorName = ("mpfDetectorName", "MppMpfDetector")
    foregroundThread = ("foregroundThread", "on")
    timeout = ("timeout", "") # No timeout
    logToHost = ("log_to_host", "info")
    outToHost = ("out_to_host", "info")
    errToHost = ("err_to_host", "error")
    statusRelUrl = ("statusRelUrl", mpfTaskUrl + "/status")
    eventRelUrl = ("eventRelUrl", mpfTaskUrl + "/event")
    videoDir = "/mnt/"

    instance_count = 2
    tgt_user = "mpf1"
    reservations = prepare_ec2_exec_targets(tgt_user, instance_count, DEFAULT_EC2_INSTANCE_TYPE)
    taskCount = 0

    for res in reservations:
        tgtHost = res.instances[0].dns_name
        print "Creating exec target and submitting tasks on %s" % (tgtHost)
    
        print datetime.datetime.now().strftime("%H:%M:%S") + ": Creating new mpfExecTgt " + tgtHost 
        execTgtName = ("tgtName", tgtHost)
        execTgtIP = ("ip", tgtHost)
        postParams = execTgtName, execTgtIP, execTgtUser, execTgtPassword, execTgtLimit, execTgtPriority
#        for param in postParams:
#            print param
        try:
            print post_multipart(hostName, execTgtPostSelector, postParams, "")
            newExecTgtId = get_new_exec_tgt_id(hostName, execTgtGetSelector)
            print datetime.datetime.now().strftime("%H:%M:%S") + ": Created new mpfExecTgt with ID " + newExecTgtId
 
        except Exception, e:
            print e

#        detector = "detect-md.sh"
#        videoFile = "predator-clip-short.mpg"
#        print datetime.datetime.now().strftime("%H:%M:%S") + ": Running detector " + detector + " against video file " + videoFile + " on target " + tgtHost
#        taskName = ("name", videoFile + "-with-" + detector)
#        detectPrefix = ("detectPrefix", "detector=" + detector)
#        videoFileUrl = ("videoFileUrl", videoDir + videoFile)
#        execTgtId = ("execTgtId", newExecTgtId)
#        postParams = taskName, mpfDetectorName, detectPrefix, videoFileUrl, timeout, foregroundThread, execTgtId, logToHost, outToHost, errToHost, statusRelUrl, eventRelUrl
#        for param in postParams:
#            print param
#        try:
#            print post_multipart(hostName, taskPostSelector, postParams, "")
#            newTaskId = get_new_task_id(hostName, taskGetSelector)
#            print datetime.datetime.now().strftime("%H:%M:%S") + ": Created new task with ID " + newTaskId
#            tasks[newTaskId] = "RUNNING"
#            taskCount += 1

#        except Exception, e:
#            print e

    # Wait for tasks to complete
    #runningTasks = taskCount
    #while runningTasks > 0:
    #    for task, status in tasks.iteritems():
    #        if status == "RUNNING":
    #            taskDone = get_task_completion(hostName, taskGetSelector, task)
#                if not taskDone:
#                    print datetime.datetime.now().strftime("%H:%M:%S") + ": Task " + task + " is still running"
#                else:
#                    tasks[task] = "COMPLETE"
#                    print datetime.datetime.now().strftime("%H:%M:%S") + ": Task " + task + " is complete"
#                    runningTasks -= 1
#        time.sleep(10)
    #print datetime.datetime.now().strftime("%H:%M:%S") + ": All done"

