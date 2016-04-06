# Copyright Mellanox Technologies, Ltd. 2001-2016.
# This software product is licensed under Apache version 2, as detailed in
# the COPYING file.

SUMMARY = "SAI API"
LICENSE = "GPLv2"

inherit autotools pkgconfig

PROVIDES += "virtual/sai"
RPROVIDES_${PN} += "virtual/sai"

DEPENDS += "libtool-native autogen-native"

PREFIX = "${STAGING_DIR_HOST}/${prefix}"

EXTRA_OECONF='--with-applibs=${PREFIX}'
EXTRA_OEMAKE='"LIBTOOL=${STAGING_BINDIR_CROSS}/${HOST_SYS}-libtool"'

LIC_FILES_CHKSUM = " \
    file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6; \
"

S = "${WORKDIR}/git/stub"
B = "${S}"

SRCREV = "${AUTOREV}"

SRC_URI = " \
    git://github.com/opencomputeproject/SAI.git;protocol=https;branch=master \
    file://0003-Added-pkgconfig-support.patch;patch=1;pnum=2 \
    file://sai-ocp-foreign.patch;patch=1;pnum=2 \
"

FILES_${PN} += "${prefix}/share"
