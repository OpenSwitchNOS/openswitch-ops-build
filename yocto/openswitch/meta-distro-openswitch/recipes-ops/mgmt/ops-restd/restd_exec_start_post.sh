#!/bin/bash

COUNTER=$(netstat -a | grep -c restd);
retry=0

while [ $COUNTER -lt 1 ]; do
    COUNTER=$(netstat -a | grep -c restd);
    let retry+=1
    if [ $COUNTER -eq 1 ];
        then /usr/sbin/setcap -r /usr/bin/python2.7;
    fi;
    if [ $retry -eq 1000 ];
        then exit 2
    fi;
done;
