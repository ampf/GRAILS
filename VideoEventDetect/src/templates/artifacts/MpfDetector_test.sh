#!/bin/bash
#===============================================================================
#
#          FILE:  test.sh
#
#         USAGE:  ./test.sh
#
#   DESCRIPTION:  Sources detect.sh and executes 'detect'
#                 passing in any args
#
#       OPTIONS:  ---
#  REQUIREMENTS:  ---
#          BUGS:  ---
#         NOTES:  ---
#        AUTHOR: Wayne Stidolph, wstidolph@appscio.com
#       COMPANY:
#     COPYRIGHT:  Copyright (c) 2009 Appscio, Inc  All Rights reserved
#       VERSION:  1.0
#       CREATED:  12/16/2009 04:32:31 PM PST
#      REVISION:  ---
#===============================================================================
#
# you can set eventEndpoint and statusEndpoint as env vars if you want curl to
# actually send something
# statusEndpoint=
# eventEndpoint=
echo "will load " ${script="detect.sh"}
echo "will execute " ${detect_fn="detect"}

use_gpu='if_avail' # other choices are 'do_not_use' and 'required'


cd ..
JOBDIR=`pwd`

function mpf_log {
  echo `date` "$1" >> $JOBDIR/JOB.LOG
  echo $1
}

function mpf_fn_exists {
    type -t $1 | grep -q 'function'
}

# replace the sendStatus and sendEvent functions with a local just-log version
function mpf_sendStatus {
  mpf_log "sending status $1 \"$2\" to $statusEndpoint"
  HTTP_DIR=$JOBDIR/"http"
  if [ ! -e $HTTP_DIR ]; then
    mkdir $HTTP_DIR
  fi
  STATUS_FILE="$HTTP_DIR/STATUS_RESP_"`date +%H_%M_%S_%N`
  rtncode='NOT RUN'
  if [ -n "$statusEndpoint" ] ; then
       rtncode=`curl -o "$STATUS_FILE" --retry 1 -w "%{http_code} from %{url_effective}" --request PUT -d state="$1" -d msg="$2" $statusEndpoint`
  fi
  mpf_log "curl returns ($rtncode) see response in $STATUS_FILE"
}

function mpf_sendEvent {
      log "sending event to $eventEndpoint; value is $1"
}

cd scripts

source "./$script"

mpf_fn_exists "$detect_fn"
if [ $? -eq 0 ]; then
  $detect_fn
else
  mpf_sendStatus 'no detect function found'
fi
