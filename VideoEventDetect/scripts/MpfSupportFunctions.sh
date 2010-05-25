#!/bin/bash
#*******************************************************************************
# Copyright Appscio, Inc (c)  - 2010 All rights reserved.
# This text is made available according to the terms of the Appscio Customer License Ver 1.0 which accompanies this distribution and is available at http://www.appscio.com/reference/appscio-license.html
#******************************************************************************/
#===============================================================================
#
#          FILE:  MpfSupportFunctions.sh
#
#         USAGE:  source MpfSupportFunctions.sh
#
#   DESCRIPTION:  send status/events, provide timeout/heartbeat, misc support
#
#       OPTIONS:  ---
#  REQUIREMENTS:  ---
#          BUGS:  ---
#         NOTES:  ---
#        AUTHOR:  Wayne Stidolph
#       COMPANY:  Appscio, Inc
#     COPYRIGHT:  Copyright (c) 2010 Appscio, Inc  All Rights reserved
#       VERSION:  1.1
#       CREATED:  01/06/2010 04:15:00 PM PST
#      REVISION:  20100424 WES add more elapsedSec return points
#===============================================================================
#
function mpf_set_identity {
   mpf_rpt_usr="$1"
   mpf_rpt_pw="$2"
   mpf_rpt_name="$3"
   mpf_log "set username/pw to $mpf_rpt_usr / $mpf_rpt_pw, with mpf_name of $mpf_rpt_name"
}

function mpf_establish_http_log {
  HTTP_DIR="http"
  if [ ! -d "$HTTP_DIR" ]; then
    mkdir "$HTTP_DIR"
  fi
  HTTP_LOG="$HTTP_DIR"/HTTP.LOG
  touch "$HTTP_LOG"
}

function mpf_sendStatus {
# MpfDetector plugin expects 'state' and 'msg' attributes, PUTed to the endpoint
# 'state' from here should be one of:
#        PENDING,  // task analyzed and prepped, ready to execute
#        RUNNING,  // dispatched on execution env
#        SUCCEEDED,// execution concluded OK
#        FAILED,
#        UNKNOWN,
#        STATS     // sending output from usr/bin/time
  mpf_log "sending status $1 \"$2\" to $statusEndpoint"
 [[ $1 != "elapsedSecs" && $1 != "HEARTBEAT" ]] && echo $1 > $JOBDIR/CURRENT_STATUS
  STATUS_FILE="$HTTP_DIR"/STATUS_RESP_`date +%H_%M_%S_%N`
  curlout=`curl -o "$STATUS_FILE" --retry 1 -w "%{http_code} from %{url_effective}"  --anyauth -u $mpf_rpt_usr:$mpf_rpt_pw -s --location-trusted -F "state=$1" -F "msg=$2" $statusEndpoint`
  rtncode=$?
  if [ -e "$STATUS_FILE" ]; then
    mpf_log "curl returns status=$rtncode, http rtn=($curlout) see response in $STATUS_FILE"
    cat $STATUS_FILE >> $HTTP_LOG
  else
    mpf_log "curl returns status=$rtncode,  http rtn=($curlout) no file captured (expected $STATUS_FILE)"
  fi
}

function mpf_sendEvent {
  EVENT_FILE="$HTTP_DIR/EVENT_RESP_"`date +%H_%M_%S_%N`
  mpf_log "sending event \"$1\" to $eventEndpoint"
  curlout=`curl -o "$EVENT_FILE" --retry 1 -w "%{http_code} from %{url_effective}"  --anyauth -u $mpf_rpt_usr:$mpf_rpt_pw -s --location-trusted -F "event=$1" $eventEndpoint`
  rtncode=$?
  if [ -e "$EVENT_FILE" ]; then
    mpf_log "curl returns status=$rtncode, http rtn=($curlout) see response in $EVENT_FILE"
    cat $EVENT_FILE >> $HTTP_LOG
  else
    mpf_log "curl returns status=$rtncode,  http rtn=($curlout) no file captured (expected $EVENT_FILE)"
  fi
  mpf_update_TIME
}

function mpf_sendEventXMLFile {
  EVENT_FILE="$HTTP_DIR/EVENT_RESP_"`date +%H_%M_%S_%N`
  mpf_log "sending event \"$1\" to $eventEndpoint"
  curlout=`curl -o "$EVENT_FILE" --retry 1 -w "%{http_code} from %{url_effective}"  --anyauth -u $mpf_rpt_usr:$mpf_rpt_pw -s --location-trusted --request POST -H "Content-type: text/xml" -d @"$1" $eventEndpoint`
  rtncode=$?
  if [ -e "$EVENT_FILE" ]; then
    mpf_log "curl returns status=$rtncode, http rtn=($curlout) see response in $EVENT_FILE"
    cat $EVENT_FILE >> $HTTP_LOG
  else
    mpf_log "curl returns status=$rtncode,  http rtn=($curlout) no file captured (expected $EVENT_FILE)"
  fi
  mpf_update_TIME
}

function mpf_sendLoggable {
# MpfDetector plugin expects 'state' and 'msg' attributes, PUTed to the endpoint
# 'state' from here should be one of:
#        LOG,  // sending a LOG message
#        OUT,  // sending a 'stdout' string
#        ERR,  // sending a 'stderr' string
  LOGGABLE_FILE="$HTTP_DIR/LOGGABLE_RESP_"`date +%H_%M_%S_%N`
  curlout=`curl -o "$LOGGABLE_FILE" --retry 1 --anyauth -u $mpf_rpt_usr:$mpf_rpt_pw -s --location-trusted -F "state=$1" -F "msg=[$jobname] $2" $statusEndpoint`
  rtncode=$?
  if [ $rtncode -ne 0 ] ; then
    echo "curl sending loggable returned status [ $rtncode ] and result [ "$curlout" ]" >> $JOBDIR/JOB.LOG
  fi
  if [ -e "$LOGGABLE_FILE" ] ; then
    cat "$LOGGABLE_FILE" >> "$HTTP_LOG"
    echo "" >> "$HTTP_LOG"
    rm "$LOGGABLE_FILE"
  fi
}


function mpf_log {
  echo `date` "$1" >> $JOBDIR/JOB.LOG
  if [[ ${#host_log} -gt 0 && ! "$host_log" =~ "off" ]] ; then
    mpf_sendLoggable "LOG" "$1"
  fi
}

function mpf_fn_exists {
    type -t "$1" | grep -q 'function'
}

function mpf_start_TIME {
  start_time=$(date +%s)
}

function mpf_update_TIME {

  stop_time=$(date +%s)
  duration=$((stop_time - start_time))
  echo $duration > TIME
  mpf_sendStatus "elapsedSecs" "`cat TIME`"
}

function mpf_start_heartbeat {
  pid_to_watch=$1
  mpf_log "heartbeat watching pid $pid_to_watch (and children)"
  touch HEARTBEAT

  while [ -f "HEARTBEAT" ] ; do
    # track the CPU used (total and %), what processor it's on (psr), the memory (resident abs and %)
    msg=`ps -m -o pid,time,pcpu,psr,rss,pmem,comm -p $pid_to_watch --ppid $pid_to_watch`
    echo "$msg" >> "HEARTBEAT_LOG"
    mpf_sendStatus "HEARTBEAT" "$msg"
    mpf_update_TIME
    sleep 1m
  done
}

function mpf_stop_heartbeat {
  rm -f HEARTBEAT
  echo "stopping heartbeat" >> "HEARTBEAT_LOG"
}

function mpf_is_valid_sleep_param {
  # test if $1 is a valid param to 'sleep'
  if [ $# -ne 1 ] ; then
    mpf_log "mpf_is_valid_sleep_param expects one parameter, got $# "
    return false
  fi
  if [ "$1" =~ ^[1-9] ] ; then return true ; fi
  return false
}

function mpf_cmd_no_timeout {
  if [ $# -ne 1 ] ; then
     mpf_log "cmd_timeout expects 1 parameter, got $#"
     return -1
  fi
  command=$1
  mpf_log "running [$command] with no timeout"
  mpf_start_heartbeat $$ &  # monitor 'this process'

  # cmd_status=`/usr/bin/time -o TIMES "${command}"`

  cmd_status=`"${command}"`
  mpf_update_TIME

  mpf_stop_heartbeat
  return cmd_status
}

# Based on technique from
# http://typo.pburkholder.com/2009/06/22/bash-command-timeout
function mpf_cmd_timeout {
   if [ $# -lt 2 ] ; then
     mpf_log "mpf_cmd_timeout expects at least 2 parameters, got $#"
     return -1
   fi
   command="$1"
   sleep_time="$2"

   mpf_log "running [$command] with timout [$sleep_time]"

   # run $command in background, sleep for our timeout then kill the process if it is running
   # $! has the pid of the backgrounded job
   "${command}" &
   cmd_pid=$!

   mpf_start_heartbeat $$ &

   # sleep for our timeout then kill the process if it is running
   ( sleep $sleep_time && kill "$cmd_pid" && mpf_sendStatus "FAILED" "ERROR - killed $command due to timeout [$sleep_time] exceeded" ) &
   killer_pid=$!

   # 'wait' for cmd_pid to complete normally.  If it does before the timeout is reached, then
   # the status will be zero.  If the killer_pid terminates it, then it will have a non-zero
   # exit status
   wait "$cmd_pid" &> /dev/null
   wait_status=$?
   mpf_update_TIME

   if [ $wait_status -ne 0 ]; then
      mpf_sendStatus "FAILED" "WARNING - command $command had unclean exit $wait_status"
   else
      # Normal exit, detach and clean up the useless killer_pid
      disown $killer_pid
      kill $killer_pid &> /dev/null
   fi

   return $wait_status
}

function mpf_cleanup {
  mpf_stop_heartbeat
  #rm -f scripts/OUT
  #rm -f scripts/ERR
  cd $JOBDIR/..
  DONE_DIR="completedMpfJobs"
  OLD_JOBS_DIR="$DONE_DIR"/prev
  if [ ! -e "$DONE_DIR" ]; then
    mkdir "$DONE_DIR"
    mv "$JOBDIR" "$DONE_DIR"
  else
    if [ ! -e "$OLD_JOBS_DIR" ]; then
      mkdir "$OLD_JOBS_DIR"
    fi
    mv "$DONE_DIR"/* "$OLD_JOBS_DIR" # ignore move-to-self error
    mv "$JOBDIR" "$DONE_DIR"
    JOBDIR="$DONE_DIR" #for any functions still running
  fi
  sleep 5
  cd "$JOBDIR/scripts"
  rm OUT
  rm ERR
	exit 0
}

function mpf_batch_pipe_out {
    # process stdin and send to mpf_sendLoggable
    while true; do
        local rpt=
        local line=
        while read line ; do
            echo $line >> STDOUT
            if [[ ${#host_out} -gt 0 && ! "$host_out" =~ "off" ]] ; then
#                rpt="$rpt\n"$line
#                echo "IN TEST WITH ${#rpt}" >> STDOUT
#               if [ ${#rpt} -gt 0 ] ; then
                    mpf_sendLoggable "OUT" "$line"
#                fi
            fi
        done
#        sleep 5
    done
}

function mpf_batch_pipe_err {
    # process stdin and send to mpf_sendLoggable
    while true ; do
        local rpt=
        local line=
        while read line ; do
            echo $line >> STDERR
            if [[ ${#host_err} -gt 0 && ! "$host_err" =~ "off" ]] ; then
#               if [ ${#rpt} -gt 0 ] ; then
                    mpf_sendLoggable "ERR" "$line"
#               fi
#               rpt="$rpt\n"$line
            fi
        done
 #       sleep 5
    done
}
