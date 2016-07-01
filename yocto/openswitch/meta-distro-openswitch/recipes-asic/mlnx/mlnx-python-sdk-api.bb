# Copyright Mellanox Technologies, Ltd. 2001-2016.

SUMMARY = "Python SDK API module"
LICENSE = "Proprietary"

inherit openswitch mellanox

RDEPENDS_${PN} += "mlnx-applibs mlnx-sx-gen-utils mlnx-sxd-libs mlnx-sx-complib"

SRC_URI = " \
    ${SX_SDK_URI}/mlnx-python-sdk-api-${SX_SDK_VERSION}.tar.gz;subdir=mlnx-python-sdk-api-${SX_SDK_VERSION} \
"

SRC_URI[md5sum] = "034bef53d838886e46e947a1b556a51b"
SRC_URI[sha256sum] = "af0cb6de181f4b539f58e573fe3de6f41a4da9bfef49ed9325ec002296a5ae32"

S = "${WORKDIR}/mlnx-python-sdk-api-${SX_SDK_VERSION}/"

LIC_FILES_CHKSUM = " \
    file://${S}/License.txt;md5=801f80980d171dd6425610833a22dbe6;  \
"

# Skip the unwanted steps
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${prefix}/bin/
    install -d ${D}${libdir}

    cp -R ${S}/usr/bin/* ${D}${prefix}/bin/
    chmod -R 755 ${D}${prefix}/bin/

    cp -R ${S}/usr/lib/* ${D}${libdir}
    chmod -R 755 ${D}${libdir}
}

# Add the .so to the main package’s files list
FILES_${PN} += "${libdir}/* ${bindir}/*"
# Make sure it isn’t in the dev package’s files list
FILES_SOLIBSDEV = ""

INSANE_SKIP_${PN} = "dev-so"

# Avoid QA Issue: already-stripped
INSANE_SKIP_${PN} += "already-stripped"
