#!/bin/bash
#===============================================================================
#
#          FILE:  detect.sh
#
#         USAGE:  sourced into a wrapper and called as just 'detect' function
#
#   DESCRIPTION:  This is a multiple-event MpfDetector for generating test events
#
#       OPTIONS:  env vars:
#                 count (number of events to generate, defaults to 10)
#                 int_dist (interval distribution model, RANDOM, UNIFORM; default UNIFORM)
#                 int_max (interval max, or interval for UNIFORM; 1/200 sec unit; default 200)
#  REQUIREMENTS:  ---
#          BUGS:  ---
#         NOTES:  ---
#        AUTHOR: Wayne Stidolph
#       COMPANY: Appscio
#     COPYRIGHT: Copyright (c) 2010, Appscio, Inc  All Rights Reserved
#       VERSION:  1.0
#       CREATED:  12/16/2009 04:12:06 PM PST
#      REVISION:  ---
#===============================================================================

function detect {
        mpf_sendStatus "RUNNING" "start MultiTesterMpfDetector"
        echo "this shows we capture std out"
        echo "this shows we capture stderr the normal bash way" 1>&2

        int_dist=${int_dist:="UNIFORM"}
        int_max=${int_max:=200}
        count=${count:=10}
        declare -a Intervals
        mpf_log "int_dist is $int_dist , int_max is $int_max , count is $count"

        buildIntervals $count $int_dist
        interval_as_string=""
        for i in $( seq $count )
        do
          interval_as_string="$interval_as_string,${Intervals[$i]}"
        done

        mpf_sendEvent "built Intervals of $interval_as_string"

        SECONDS=0
        for ((i=1; i<=${count}; i++)); do
          # test event goes back with its index, actual time since last event,
          # and planned time to next event
          timeUntil=${Intervals[$i]}
          mpf_sendEvent "event: $i,$SECONDS,$timeUntil"
          SECONDS=0
          sleep $timeUntil
        done
        mpf_sendStatus "SUCCEEDED" "done with MultiTesterMpfDetector"
}

function buildIntervals {
  for x in $( seq $1 )
  do
    if [ "$2" == "RANDOM" ]
    then
        Intervals[$x]=$RANDOM
        let  "Intervals[$x] = ${Intervals[$x]} % $int_max"
    else
        Intervals[$x]=$int_max
    fi
  done
  # now convert to hundredth-second intervals
  for x in $( seq $1 )
  do
    Intervals[$x]=`echo "scale=2 ; ${Intervals[$x]} / 100.0" | bc`
  done
}