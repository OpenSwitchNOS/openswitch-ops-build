# Copyright (C) 2016 Hewlett Packard Enterprise Development LP
#!/bin/bash

while [[ $(ps -ef | grep systemd-coredump | grep $1 | grep -v grep) ]]
do
    sleep 5
done
/sbin/reboot -f
