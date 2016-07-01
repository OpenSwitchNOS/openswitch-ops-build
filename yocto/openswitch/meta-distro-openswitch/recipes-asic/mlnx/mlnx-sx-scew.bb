# Copyright Mellanox Technologies, Ltd. 2001-2016.

SUMMARY = "Scew 1.1.2 Libraries required for switch sx_examples"
LICENSE = "LGPLv2.1"

inherit openswitch

LIC_FILES_CHKSUM = " \
    file://${COMMON_LICENSE_DIR}/LGPL-2.1;md5=801f80980d171dd6425610833a22dbe6;  \
"

RDEPENDS_${PN} += "expat"

SX_SDK_VERSION ??= ""
SX_SDK_URI ??= "file://"

SRC_URI = " \
    ${SX_SDK_URI}/mlnx-sx-scew-${SX_SDK_VERSION}.tar.gz;subdir=mlnx-sx-scew-${SX_SDK_VERSION} \
"

SRC_URI[md5sum] = "464b067a798a3c16baeed9d111a1b7fc"
SRC_URI[sha256sum] = "0fb7da11b44ceaeb04f62f55fa80b9885b161a70a80d3d5130c58e0b19c2bb54"

S = "${WORKDIR}/mlnx-sx-scew-${SX_SDK_VERSION}/"

# Skip the unwanted steps
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${prefix}/include/
    install -d ${D}${libdir}

    cp -R ${S}/usr/include/* ${D}${prefix}/include/
    chmod -R 755 ${D}${prefix}/include/

    cp -R ${S}/usr/lib/* ${D}${libdir}
    chmod -R 755 ${D}${libdir}
}

FILES_${PN} += "${prefix}/include/ ${libdir}"

# Add the .so to the main package’s files list
FILES_${PN} += "${libdir}/*.so"
# Make sure it isn’t in the dev package’s files list
FILES_SOLIBSDEV = ""

INSANE_SKIP_${PN} = "dev-so"

# Avoid QA Issue: already-stripped
INSANE_SKIP_${PN} += "already-stripped"
