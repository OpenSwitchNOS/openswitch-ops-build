# Copyright (C) 2016 Hewlett Packard Enterprise Development LP

PR_append = "_as5812x"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "\
	file://onie.config \
"

IMAGE_NAME = "openswitch-disk-image"
ONIE_PREFIX = "x86_64-as5812_54x"
