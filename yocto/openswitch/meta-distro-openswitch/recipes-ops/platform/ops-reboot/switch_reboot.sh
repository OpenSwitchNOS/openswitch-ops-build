#!/bin/bash

while [[ $(ps -ef | grep systemd-coredump | grep $1 | grep -v grep) ]]
do
    sleep 5
done
systemctl stop switchd
systemctl stop ops-bgpd
systemctl stop ovsdb-server
/sbin/reboot -f
