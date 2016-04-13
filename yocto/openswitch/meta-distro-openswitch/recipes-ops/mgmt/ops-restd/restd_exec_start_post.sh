# (C) Copyright 2016 Hewlett-Packard Enterprise Development Company, L.P.
#/bin/bash

MAX_RETRIES=5
PROCESS_NAME=restd

# This loop it waits until the restd is Up.
# Does not matter condition of success or failure,
# the capabilities are going to be removed
for ((i=1; i<=$MAX_RETRIES; i++)); do
    COUNTER=$(netstat -a | grep -c $PROCESS_NAME)
    if [ $COUNTER -ge 1 ]; then
        break
    fi
    sleep 1
done;

/usr/sbin/setcap -r /usr/bin/python2.7;
