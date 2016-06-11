# Copyright (C) 2015 Hewlett Packard Enterprise Development LP

PR_append = "_dx010"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append = "\
    file://defconfig \
    file://driver-celestica-redstone-xp.patch \
    file://driver-support-new-broadcom-phys.patch \
    file://driver-support-intel-avoton-ethernet-with-broadcom-phy.patch \
"