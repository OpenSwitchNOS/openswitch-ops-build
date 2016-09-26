# Copyright (C) 2015 Hewlett Packard Enterprise Development LP

PR_append = "_as5712"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://Makefile-modules"

OPENNSL_PLATFORM = "as5712"
OPENNSL_PLATFORM_BUILD = "c790638"
GPL_MODULES_DIR = "sdk-6.4.10-gpl-modules"

SRC_URI[md5sum] = "1ce64fe2fba13b1fceefd98527a960a6"
SRC_URI[sha256sum] = "dff301b6f0850de13393db18b34bc3f1ba7f395d0d657deff925b447f18bbe56"
