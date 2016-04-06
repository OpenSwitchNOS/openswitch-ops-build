# Copyright Mellanox Technologies, Ltd. 2001-2016.
# This software product is licensed under Apache version 2, as detailed in
# the COPYING file.

KBRANCH_sai-stub  = "standard/common-pc-64/base"
KMACHINE_sai-stub ?= "common-pc-64"

SRCREV_machine_sai-stub ?= "dbe5b52e93ff114b2c0f5da6f6af91f52c18f2b8"

COMPATIBLE_MACHINE_sai-stub = "sai-stub"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://ops-fragment.cfg \
	file://openvswitch.cfg \
        file://strongswan-fragment.cfg \
        file://ops-audit.cfg \
"
