KERNEL_IMAGETYPE_usermode = "linux"
KERNEL_OUTPUT_usermode = "linux"
KERNEL_MODE_usermode = "0755"
INHIBIT_PACKAGE_STRIP = "1"

COMPATIBLE_MACHINE_usermode = "usermode"

PR_append = "_usermode"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append = "\
    file://defconfig \
"
