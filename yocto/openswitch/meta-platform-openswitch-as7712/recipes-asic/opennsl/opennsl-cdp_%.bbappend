# Copyright (C) 2016 Hewlett Packard Enterprise Development LP

PR_append = "_as7712"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://Makefile-modules"

OPENNSL_PLATFORM = "as7712"
OPENNSL_PLATFORM_BUILD = "c790638"
GPL_MODULES_DIR = "sdk-6.4.10-gpl-modules"

SRC_URI[md5sum] = "f66482d0414ab22f38061338c3947f2a"
SRC_URI[sha256sum] = "2dbb8a98f793f4e33421539871c496ad29f0f8242ccffa0e0263e10ea62d7229"
