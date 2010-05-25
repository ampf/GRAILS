#!/bin/bash
# adding shebang just to get VIM to do color-coding (detect this as bash stuff)
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
#       VERSION:  1.0
#       CREATED:  01/06/2010 04:15:00 PM PST
#      REVISION:  ---
#===============================================================================
#
function mpf_sendStatus {
# MpfDetector plugin expects 'state' and 'msg' attributes, PUTed to the endpoint
# 'state' from here should be one of:
#        PENDING,  // task analyzed and prepped, ready to execute
#        RUNNING,  // dispatched on execution env
#        SUCCEEDED,// execution concluded OK
#        FAILED,
#        UNKNOWN
  mpf_log "sending status $1 \"$2\" to $statusEndpoint"
  HTTP_DIR="$JOBDIR"/"http"
  if [ ! -e "$HTTP_DIR" ]; then
    mkdir "$HTTP_DIR"
  fi
  STATUS_FILE="$HTTP_DIR/STATUS_RESP_"`date +%H_%M_%S_%N`
# --trace-ascii "$STATUS_FILE"_TRACE
  rtncode=`curl -o "$STATUS_FILE" --retry 1 -w "%{http_code} from %{url_effective}"  -s --location -F "state=$1" -F "msg=$2" $statusEndpoint`
  mpf_log "curl returns ($rtncode) see response in $STATUS_FILE"
}

function mpf_sendEvent {
  HTTP_DIR=$JOBDIR/"http"
  EVENT_FILE="$HTTP_DIR/EVENT_RESP_"`date +%H_%M_%S_%N`
  mpf_log "sending event \"$1\" to $eventEndpoint"
  rtncode=`curl -o "$EVENT_FILE" --retry 1 -w "%{http_code} from %{url_effective}" -s --location -F "event=$1" $eventEndpoint`
  mpf_log "curl returns ($rtncode) see response in $EVENT_FILE"
}

function mpf_sendEventXMLFile {
  HTTP_DIR=$JOBDIR/"http"
  EVENT_FILE="$HTTP_DIR/EVENT_RESP_"`date +%H_%M_%S_%N`
  mpf_log "sending event \"$1\" to $eventEndpoint"
  rtncode=`curl -o "$EVENT_FILE" --retry 1 -w "%{http_code} from %{url_effective}" -s --location --request POST -H "Content-type: text/xml" -d @"$1" $eventEndpoint`
  mpf_log "curl returns ($rtncode) see response in $EVENT_FILE"
}


function mpf_start_heartbeat {
  touch HEARTBEAT
  while [ -e HEARTBEAT ] ; do
    msg=`ps -m -o pid,state,time -p $cmd_pid --pid $cmd_pid`
    echo "$msg" >> $JOBDIR/HEARTBEAT_LOG
    mpf_sendStatus "HEARTBEAT" "cmd_pid $cmd_pid => $msg"
      sleep 30
  done
}

function mpf_stop_heartbeat {
  rm -f HEARTBEAT
}

# Based on technique from
# http://typo.pburkholder.com/2009/06/22/bash-command-timeout
function mpf_cmd_timeout {
   [ $# -eq 2 ] || die "cmd_timeout takes 2 arguments"
   command="$1"
   sleep_time="$2"
   mpf_log "running [$command] with timout [$sleep_time]"

   # run $command in background, sleep for our timeout then kill the process if it is running
   # $! has the pid of the backgrounded job
   $command &
   cmd_pid=$!
   mpf_start_heartbeat&

   # sleep for our timeout then kill the process if it is running
   ( sleep "$sleep_time" && kill "$cmd_pid" && mpf_sendStatus "FAILED" "ERROR - killed $command due to timeout $sleep_time exceeded" ) &
   killer_pid=$!

   # 'wait' for cmd_pid to complete normally.  If it does before the timeout is reached, then
   # the status will be zero.  If the killer_pid terminates it, then it will have a non-zero
   # exit status
   wait "$cmd_pid" &> /dev/null
   wait_status=$?

   mpf_stop_heartbeat
   if [ $wait_status -ne 0 ]; then
      mpf_sendStatus "FAILED" "WARNING - command $command had unclean exit $wait_status"
   else
      # Normal exit, detach and clean up the useless killer_pid
      disown $killer_pid
      kill $killer_pid &> /dev/null
   fi

   return $wait_status
}

function mpf_log {
  echo `date` "$1" >> $JOBDIR/JOB.LOG
}

function mpf_fn_exists {
    type -t "$1" | grep -q 'function'
}

function mpf_moveToDone {
  cd $1/..
  DONE_DIR="completedMpfJobs"
  OLD_JOBS_DIR="$DONE_DIR"/prev
  if [ ! -e "$DONE_DIR" ]; then
    mkdir "$DONE_DIR"
    mv "$1" "$DONE_DIR"
  else
    if [ ! -e "$OLD_JOBS_DIR" ]; then
      mkdir "$OLD_JOBS_DIR"
    fi
    mv "$DONE_DIR"/* "$OLD_JOBS_DIR" # ignore move-to-self error
    mv "$1" "$DONE_DIR"
  fi
}