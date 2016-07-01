# Copyright Mellanox Technologies, Ltd. 2001-2016.

SUMMARY = "Libraries for SX device management"
LICENSE = "Proprietary"

inherit openswitch mellanox

RDEPENDS_${PN} += "mlnx-sx-complib mlnx-sx-kernel"

SRC_URI = " \
    ${SX_SDK_URI}/mlnx-sxd-libs-${SX_SDK_VERSION}.tar.gz;subdir=mlnx-sxd-libs-${SX_SDK_VERSION} \
"

SRC_URI[md5sum] = "557d314aa127ee8a1c3a7930632f365e"
SRC_URI[sha256sum] = "69fa75d8d4d57368c21056428c61c9db238ee05f1a7d2a0919e6497db96f8005"

S = "${WORKDIR}/mlnx-sxd-libs-${SX_SDK_VERSION}/"

LIC_FILES_CHKSUM = " \
    file://${S}/License.txt;md5=801f80980d171dd6425610833a22dbe6;  \
"

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
