# Copyright (C) 2015 Hewlett Packard Enterprise Development LP

PR_append = "_dx010"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "\
	file://onie.config \
"

IMAGE_NAME = "openswitch-disk-image"
ONIE_PREFIX = "x86_64-cel_seastone"
