#!/bin/sh
#
#  Copyright (C) 2016 Hewlett Packard Enterprise Development LP
#
#cdm: Code Dump Manager
#This script is used to throttle the number of core files for each daemon
# configure maxcore=N in ops_corefile.conf , where N is maximum number of core

CORE_CONF_FILE=/tmp/ops_corefile.conf
LIMIT=5    #default maximum limt

PROCESS_NAME=$1
PROCESS_PID=$2
TIME_STAMP=$3
SIGNAL=$4

# For the file naming convention we always print double digit signal number.
# Single digit signal number will prepended by 0 to make double digit.
SIGNAL=$(printf "%2.2d" $SIGNAL)

CORE_NO=0

#parse from config file
SYSTEM_LIMIT=$(grep maxcore  ${CORE_CONF_FILE} | cut -d "=" -f2 )
ARCHIVED_CORE=$(grep corepath  ${CORE_CONF_FILE} | cut -d "=" -f2 )

TIME_STAMP_FMT=$(date -d @${TIME_STAMP} "+%Y%m%d.%H%M%S")
if  ((SYSTEM_LIMIT >= 0)); then
     LIMIT=$SYSTEM_LIMIT
fi


if [ ! -d "${ARCHIVED_CORE}/${PROCESS_NAME}" ]; then
    mkdir -p $ARCHIVED_CORE/${PROCESS_NAME} 2> /dev/null
fi

COUNT=$(ls ${ARCHIVED_CORE}/${PROCESS_NAME}/${PROCESS_NAME}*core.tar.gz  2> /dev/null | wc -l)

((COUNT++))

if  (( COUNT >= LIMIT )) ; then
    CORE_NO=$LIMIT
else
    CORE_NO=$COUNT
fi

CORE_DIR=${ARCHIVED_CORE}/${PROCESS_NAME}
# corefile format is <daemon name>.<core number >.<timestamp>.<signal no. >.core.tar.gz
CORE_FILE=${CORE_DIR}/${PROCESS_NAME}.${CORE_NO}.${TIME_STAMP_FMT}.${SIGNAL}.core

rm -f  ${CORE_DIR}/${PROCESS_NAME}.${CORE_NO}.*
cat >  ${CORE_FILE}

# remove last core. We dont know exact file name due to time stamp

# archive additional logs
tar -czvf  ${CORE_FILE}.tar.gz ${CORE_FILE}  /tmp/os-release
if [ $? -eq 0 ] ; then
    rm -f  ${CORE_FILE}
fi
