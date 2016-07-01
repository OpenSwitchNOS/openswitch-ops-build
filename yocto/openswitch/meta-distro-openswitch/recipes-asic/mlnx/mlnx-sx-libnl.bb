# Copyright Mellanox Technologies, Ltd. 2001-2016.

SUMMARY = "Libnl 3.2.14 Libraries required for MLNX applibs"
LICENSE = "LGPLv2.1"

inherit openswitch

PROVIDES += "libnl"
RPROVIDES_${PN} += "libnl"

LIC_FILES_CHKSUM = " \
    file://${COMMON_LICENSE_DIR}/LGPL-2.1;md5=801f80980d171dd6425610833a22dbe6;  \
"

SRC_URI[md5sum] = "bbceff67a9682ff09e5e62a04616f9a5"
SRC_URI[sha256sum] = "1d718b63eeea16bd294437446d3a67539817bd250307b1cf88edb7e338103df2"

SX_SDK_VERSION ??= ""
SX_SDK_URI ??= "file://"

SRC_URI = " \
    ${SX_SDK_URI}/mlnx-sx-libnl-${SX_SDK_VERSION}.tar.gz;subdir=mlnx-sx-libnl-${SX_SDK_VERSION} \
"

PACKAGES += "${PN}-cli ${PN}-route ${PN}-nf ${PN}-genl ${PN}-idiag"

S = "${WORKDIR}/mlnx-sx-libnl-${SX_SDK_VERSION}/"

# Skip the unwanted steps
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${prefix}/sbin/
    install -d ${D}${prefix}/include/
    install -d ${D}${libdir}

    cp -R ${S}/usr/sbin/* ${D}${prefix}/sbin/
    chmod -R 755 ${D}${prefix}/sbin/

    cp -R ${S}/usr/include/* ${D}${prefix}/include/
    chmod -R 755 ${D}${prefix}/include/

    cp -R ${S}/usr/lib/* ${D}${libdir}
    chmod -R 755 ${D}${libdir}
}

FILES_${PN} += "${prefix}/sbin/ ${prefix}/include/ ${libdir}/ ${libdir}/libnl/"

# Add the .so to the main package’s files list
FILES_${PN} += "${libdir}/*.so"
FILES_${PN} += "${libdir}/libnl/*.so"
# Make sure it isn’t in the dev package’s files list
FILES_SOLIBSDEV = ""

INSANE_SKIP_${PN} = "dev-so"

# Avoid QA Issue: already-stripped
INSANE_SKIP_${PN} += "already-stripped"
