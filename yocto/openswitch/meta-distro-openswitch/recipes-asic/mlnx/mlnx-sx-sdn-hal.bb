# Copyright Mellanox Technologies, Ltd. 2001-2016.

SUMMARY = "Libraries for SX SDN HAL"
LICENSE = "Proprietary"

inherit openswitch

RDEPENDS_${PN} += "mlnx-sx-libnl mlnx-sxd-libs mlnx-sx-complib mlnx-applibs mlnx-sx-gen-utils mlnx-sx-acl-rm"

SX_SDK_VERSION ??= ""
SX_SDK_URI ??= "file://"

SRC_URI = " \
    ${SX_SDK_URI}/mlnx-sx-sdn-hal-${SX_SDK_VERSION}.tar.gz;subdir=mlnx-sx-sdn-hal-${SX_SDK_VERSION} \
"

SRC_URI[md5sum] = "a7f5b4c958ac4dab674798db42aba21b"
SRC_URI[sha256sum] = "4f9b016746fe8470a0f9f3eede538f97b93016833746079687456e488f74c167"

S = "${WORKDIR}/mlnx-sx-sdn-hal-${SX_SDK_VERSION}/"

LIC_FILES_CHKSUM = " \
    file://${S}/License.txt;md5=801f80980d171dd6425610833a22dbe6;  \
"

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
