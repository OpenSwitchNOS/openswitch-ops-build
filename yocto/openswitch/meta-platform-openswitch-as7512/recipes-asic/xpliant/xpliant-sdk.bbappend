# Copyright (C) 2016, Cavium, Inc. All Rights Reserved.

PR_append = "_as7512"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

FILES_${PN} += "\
    /lib/modules/${KERNEL_VERSION}/extra \
    /lib/modules/${KERNEL_VERSION}/extra/xp80-Pcie-Endpoint.ko \
"

do_install_append() {
    install -d ${D}/lib/modules/${KERNEL_VERSION}/extra
    install -m 0644 ${S}/lib/modules/as7512/xp80-Pcie-Endpoint.ko ${D}/lib/modules/${KERNEL_VERSION}/extra
}

inherit module-base
