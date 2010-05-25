#!/bin/bash
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
#       VERSION:  1.0
#       CREATED:  01/06/2010 04:15:00 PM PST
#      REVISION:  epnrl-200 collect/return timining information
#===============================================================================
## Run_<detectorname> runs in <target_user_home>/<job dir>/VadScripts/
# This script runs the target script, and posts status back to the originating app

# source the supporting files
source MpfSupportFunctions.sh
cd .. # move up to the job directory
JOBDIR=`pwd`
cd scripts # run from scripts/ so relative paths to lib etc work in dev env, too

statusEndpoint=STATUS_ENDPOINT
eventEndpoint=EVENT_ENDPOINT
video=VIDEO_FILE
timeout=TIMEOUT_SEC
use_gpu=USE_GPU
time_format='TIME_FORMAT'
detector_script='DETECTOR_SCRIPT'

mpf_log "statusEndpoint is $statusEndpoint"
mpf_log "eventEndpoint is $eventEndpoint"
mpf_log "video is $video"
mpf_log "timeout is $timeout"
mpf_log "use_gpu is $use_gpu"
mpf_log `which java`
mpf_log `java -version`

mpf_log "time format string is [$time_format]"

rundir=`pwd`
mpf_log "start directory is $rundir"
mpf_log "the detector script is $detector_script"
mpf_log "the detector prefix will be (DETECTOR_PREFIX)"

if [ -e DETECTOR_SCRIPT ]; then
  mpf_log "sourcing DETECTOR_SCRIPT"
  source DETECTOR_SCRIPT
else
  mpf_log "no DETECTOR_SCRIPT supplied"
fi

mpf_fn_exists detect
if [ $? -eq 0 ]; then
    mpf_sendStatus "RUNNING" "launching on remote"

    mpf_log "exporting detector prefix: DETECTOR_PREFIX"
    export DETECTOR_PREFIX
    /usr/bin/time -f $time_format (mpf_cmd_timeout detect $timeout 1>STDOUT 2>STDERR) 2>TIME_OUTPUT

    cmd_status=$?
    mpf_log "cmd_status is $cmd_status"
    if [ $cmd_status -ne 0 ]; then
      mpf_sendStatus "FAILED" "detect() returned $cmd_status"
    else
      mpf_sendStatus "SUCCEEDED" "detect() exited normally"
    fi
else
    mpf_sendStatus "FAILED" "no detect() function (started at DETECTOR_SCRIPT)"
fi

trap "mpf_moveToDone $JOBDIR" INT TERM EXIT
