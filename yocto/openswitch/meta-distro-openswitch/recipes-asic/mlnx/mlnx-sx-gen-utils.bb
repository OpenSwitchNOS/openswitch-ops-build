# Copyright Mellanox Technologies, Ltd. 2001-2016.

SUMMARY = "SX Manager userspace libraries and applications"
LICENSE = "Proprietary"

inherit openswitch

RDEPENDS_${PN} += "mlnx-sx-complib"

SX_SDK_VERSION ??= ""
SX_SDK_URI ??= "file://"

SRC_URI = " \
    ${SX_SDK_URI}/mlnx-sx-gen-utils-${SX_SDK_VERSION}.tar.gz;subdir=mlnx-sx-gen-utils-${SX_SDK_VERSION} \
"

SRC_URI[md5sum] = "38f5008a64c9316e2cc729ab411c2991"
SRC_URI[sha256sum] = "04f6bdd33d3087f5c1a0ca03cec33cbe7a228a97a20bea3932731cd5ddf1f2db"

S = "${WORKDIR}/mlnx-sx-gen-utils-${SX_SDK_VERSION}/"

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
# Make sure it isn’t in the dev package’s files list
FILES_SOLIBSDEV = ""
INSANE_SKIP_${PN} = "dev-so"

# Avoid QA Issue: already-stripped
INSANE_SKIP_${PN} += "already-stripped"
