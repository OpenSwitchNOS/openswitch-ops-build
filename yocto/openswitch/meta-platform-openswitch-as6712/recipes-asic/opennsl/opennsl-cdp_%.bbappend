# Copyright (C) 2015 Hewlett Packard Enterprise Development LP

PR_append = "_as6712"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://Makefile-modules"

OPENNSL_PLATFORM = "as6712"
OPENNSL_PLATFORM_BUILD = "c790638"
GPL_MODULES_DIR = "sdk-6.4.10-gpl-modules"

SRC_URI[md5sum] = "6a40de0f3a57f9022b2c8d8992b91909"
SRC_URI[sha256sum] = "e7b39b034534af8ec240ca6316a4e8268dc8d3599ca9a16f7ecce8dc5cdb1ead"
