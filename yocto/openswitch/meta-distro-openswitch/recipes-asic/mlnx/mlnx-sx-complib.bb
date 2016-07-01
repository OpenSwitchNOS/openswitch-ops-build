# Copyright Mellanox Technologies, Ltd. 2001-2016.

SUMMARY = "Mellanox SX SDK"
LICENSE = "GPLv2 | BSD"

inherit openswitch

LIC_FILES_CHKSUM = " \
    file://${COMMON_LICENSE_DIR}/BSD;md5=801f80980d171dd6425610833a22dbe6;  \
    file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6;  \
"

SX_SDK_VERSION ??= ""
SX_SDK_URI ??= "file://"

SRC_URI = " \
    ${SX_SDK_URI}/mlnx-sx-complib-${SX_SDK_VERSION}.tar.gz;subdir=mlnx-sx-complib-${SX_SDK_VERSION} \
"

SRC_URI[md5sum] = "2bab6f34d00a5f9dccdd5ca792d54bd1"
SRC_URI[sha256sum] = "58607ae97988cedf07d9e95b0949c129bdfcef27ccf263a7706d9bc06a96fb68"

S = "${WORKDIR}/mlnx-sx-complib-${SX_SDK_VERSION}/"

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
# #Make sure it isn’t in the dev package’s files list
FILES_SOLIBSDEV = ""

INSANE_SKIP_${PN} = "dev-so"

# Avoid QA Issue: already-stripped
INSANE_SKIP_${PN} += "already-stripped"
