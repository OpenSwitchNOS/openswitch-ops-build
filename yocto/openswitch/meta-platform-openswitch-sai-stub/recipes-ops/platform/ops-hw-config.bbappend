# Copyright Mellanox Technologies, Ltd. 2001-2016.
# This software product is licensed under Apache version 2, as detailed in
# the COPYING file.

PLATFORM_PATH = "SAI-stub/X86-64"
SYSTEM_MANUFACTURER ?= "Generic-x86"
SYSTEM_PRODUCT_NAME ?= "X86-64"

do_install_append() {
    install -d ${D}${sysconfdir}/openswitch/platform/${SYSTEM_MANUFACTURER}/${SYSTEM_PRODUCT_NAME}
    for f in ${S}/${PLATFORM_PATH}/*.yaml ; do
        d=`dirname "$f"`
        n=`basename "$f"`
        cp -p "${d}/${n}" ${D}${sysconfdir}/openswitch/platform/${SYSTEM_MANUFACTURER}/${SYSTEM_PRODUCT_NAME}
    done
}
