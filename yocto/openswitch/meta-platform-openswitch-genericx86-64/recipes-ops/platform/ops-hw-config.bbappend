# Copyright (C) 2015 Hewlett Packard Enterprise Development LP

PR_append = "_genericx86_64"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

PLATFORM_PATH = "Generic-x86/X86-64"
PLATFORM_FLAVOR = "${@bb.utils.contains("IMAGE_FEATURES", "ops-p4", "P4", "",d)}"
