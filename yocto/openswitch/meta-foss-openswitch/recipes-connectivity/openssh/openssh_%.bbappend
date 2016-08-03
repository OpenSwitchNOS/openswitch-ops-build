# Copyright (C) 2015 Hewlett Packard Enterprise Development LP

PR_append = "_openswitch"

FILESEXTRAPATHS_prepend := "${THISDIR}/openssh:"

SRC_URI_append = "\
    file://bypass-getpwnam-check.patch \
"

RDEPENDS_${PN} += "${PN}-sftp ${PN}-sftp-server"
