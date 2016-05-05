# Copyright (C) 2015 Hewlett Packard Enterprise Development LP

PR_append = "_openswitch"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

hostname = "${DISTRO_SHORTNAME}-${MACHINE}"

# We do not use tmp as symlink
volatiles = "log"
dirs1777 = "/tmp ${localstatedir}/tmp"
