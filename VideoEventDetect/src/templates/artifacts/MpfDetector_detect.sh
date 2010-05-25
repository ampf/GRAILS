#!/bin/bash
#===============================================================================
#
#          FILE:  detect.sh
#
#         USAGE:  sourced into a wrapper and called as just 'detect' function
#
#   DESCRIPTION:  This is a super-simple MpfDetector
#
#       OPTIONS:  ---
#  REQUIREMENTS:  ---
#          BUGS:  ---
#         NOTES:  ---
#        AUTHOR:
#       COMPANY:
#     COPYRIGHT: Copyright (c) 2009, Appscio, Inc  All Rights Reserved
#       VERSION:  1.0
#       CREATED:  12/16/2009 04:12:06 PM PST
#      REVISION:  ---
#===============================================================================

function detect {
        mpf_sendStatus "RUNNING" "hello from the @artifact.name@ example detect function"
        mpf_sendEvent "executed dummy event detect"
        echo "this shows we capture std out"
        echo "this shows we capture std err the normal bash way" 1>&2
        mpf_sendStatus "RUNNING" "bye from the @artifact.name@ example detect function"
}
