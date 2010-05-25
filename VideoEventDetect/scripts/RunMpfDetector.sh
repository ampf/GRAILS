#!/bin/bash
#*******************************************************************************
# Copyright Appscio, Inc (c)  - 2010 All rights reserved.
# This text is made available according to the terms of the Appscio Customer License Ver 1.0 which accompanies this distribution and is available at http://www.appscio.com/reference/appscio-license.html
#******************************************************************************/


#===============================================================================
#
#          FILE:  RunMpfDetector.sh
#
#         USAGE:  RunMpfDetector
#
#   DESCRIPTION:  entry point for MpfDetectors on MpfExecTgt
#
#       OPTIONS:  ---
#  REQUIREMENTS:  ---
#          BUGS:  ---
#         NOTES:  ---
#        AUTHOR:  Wayne Stidolph
#       COMPANY:  Appscio, Inc
#     COPYRIGHT:  Copyright (c) 2010 Appscio, Inc  All Rights reserved
#       VERSION:  2.1
#       CREATED:  02/12/2010 04:36:00 PM PST
#      REVISION:  20100424 use new mpf_update_time function to return elapsedSec status
#===============================================================================
## Run_<detectorname> runs in <target_user_home>/<job dir>/VadScripts/
# This script runs the target script, and posts status back to the originating app
# This script is rewritten for each MpfTask run, with the ant tokens
# (@ ... @ variables) in this 'template' replace according to the MpfTask
# instance settings


jobname=@JOBNAME@ # e.g., 'vad_foo_1234567890'

# get the terminal (e.g., /dev/pty/6) in case we need to reconnect
terminal="/dev/$(ps -p$$ --no-heading | awk '{print $2}')"

# source the supporting files
source MpfSupportFunctions.sh
cd .. # move up to the job directory
JOBDIR=`pwd`
trap "mpf_cleanup" HUP INT TERM EXIT

mpf_start_TIME

cd scripts # run from scripts/ so relative paths to lib etc work in dev env, too
mpf_establish_http_log # set up the http dir and log file

statusEndpoint=@STATUS_ENDPOINT@
eventEndpoint=@EVENT_ENDPOINT@
video="@VIDEO_FILE@"
timeout="@TIMEOUT@"
use_gpu=@USE_GPU@
detector_script='@DETECTOR_SCRIPT@'
mpf_reporting_pw=@REPORTING_PW@
mpf_reporting_user=@REPORTING_USER@
mpf_task_name=@TASK_NAME@
host_log=@HOST_LOG@
host_out=@HOST_OUT@
host_err=@HOST_ERR@

if [ -n "@TIME_FORMAT@" ]; then
    export TIME="@TIME_FORMAT@"
fi

mpf_set_identity "$mpf_reporting_user" "$mpf_reporting_pw" "$mpf_task_name"

mpf_log "terminal is $terminal"
mpf_log "job name is $jobname"
mpf_log "statusEndpoint is $statusEndpoint"
mpf_log "eventEndpoint is $eventEndpoint"
mpf_log "video is [@VIDEO_FILE@]"
mpf_log "timeout is [@TIMEOUT@]"
mpf_log "TIME env var set to [$TIME]"
mpf_log "use_gpu is $use_gpu"
mpf_log `which java`
mpf_log `java -version`
mpf_log "BASH_VERSION $BASH_VERSION"

rundir=`pwd`
mpf_log "start directory is $rundir"
mpf_log "the detector script is $detector_script"
mpf_log "the detector prefix will be (@DETECTOR_PREFIX@)"
mpf_log "logging controls are HOST: $host_log OUT: $host_out ERR: $host_err"

mpf_log "exporting detector prefix: @DETECTOR_PREFIX@"
export @DETECTOR_PREFIX@

if [ -e @DETECTOR_SCRIPT@ ]; then
  mpf_log "sourcing @DETECTOR_SCRIPT@"
  source @DETECTOR_SCRIPT@ 1 > @DETECTOR_SCRIPT@.out 2> @DETECTOR_SCRIPT@.err
  mpf_log "completed sourcing @DETECTOR_SCRIPT@"
else
  mpf_log "detector script (@DETECTOR_SCRIPT@) not found"
fi

mpf_fn_exists detect
mpf_fn_exists_rtncode=$?
mpf_log "mpf_fn_exists_rtncode is ${mpf_fn_exists_rtncode}"

if [ $mpf_fn_exists_rtncode -eq 0 ]; then
    mpf_log "begin command processing"
    mpf_sendStatus "RUNNING" "launching on remote"

    if [ ! -e OUT ] ; then
      mkfifo OUT
    fi
    mpf_batch_pipe_out < OUT &
    echo "DIRECT ECHO TO OUT PIPE (STARTUP VERIFY)"   > OUT

    if [ ! -e ERR ] ; then
      mkfifo ERR
    fi
    mpf_batch_pipe_err < ERR &
    echo "DIRECT ECHO TO ERR PIPE (STARTUP VERIFY)" > ERR

    valid_sleep=mpf_is_valid_sleep_param "$timeout"
    if [ valid_sleep ] ; then
      mpf_log "valid_sleep is true"
      mpf_cmd_timeout detect "$timeout" 1>OUT 2>ERR
      cmd_status=$?
    else
       mpf_log "valid_sleep  is untrue, value is $valid_sleep"
       mpf_cmd_no_timeout detect 1>OUT 2>ERR
       cmd_status=$?
    fi

    mpf_log "cmd_status is $cmd_status"
    if [ $cmd_status -ne 0 ]; then
      mpf_sendStatus "FAILED" "detect() returned $cmd_status"
    else
      mpf_sendStatus "SUCCEEDED" "detect() exited normally"
    fi
    echo "DIRECT ECHO TO OUT PIPE (DONE-PROCESSING VERIFY)"   > OUT
    echo "DIRECT ECHO TO ERR PIPE (DONE-PROCESSING VERIFY)" > ERR
    # rm OUT
    # rm ERR
else
    mpf_log "no detect() function (started at @DETECTOR_SCRIPT@); mpf_fn_exists_rtncode = $mpf_fn_exists_rtncode"
    mpf_sendStatus "FAILED" "no detect() function (started at @DETECTOR_SCRIPT@); mpf_fn_exists_rtncode = $mpf_fn_exists_rtncode"
fi
#mpf_sendStatus "elapsedSecs" "`cat TIME`"

sleep 5 # give last communications to host time, if they need it

