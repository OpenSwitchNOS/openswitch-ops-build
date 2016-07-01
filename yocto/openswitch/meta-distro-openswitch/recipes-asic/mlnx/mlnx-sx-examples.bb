# Copyright Mellanox Technologies, Ltd. 2001-2016.

SUMMARY = "SX Examples"
LICENSE = "Proprietary"

inherit openswitch mellanox

RDEPENDS_${PN} += "mlnx-applibs"

SRC_URI = " \
    ${SX_SDK_URI}/mlnx-sx-examples-${SX_SDK_VERSION}.tar.gz;subdir=mlnx-sx-examples-${SX_SDK_VERSION} \
"

SRC_URI[md5sum] = "4988ef61b9ed3fb7fd72b5cd403f6c13"
SRC_URI[sha256sum] = "9d36cf15c1ea88def022345089d407051d2036627655bf9290ec5b1675ab268c"

S = "${WORKDIR}/mlnx-sx-examples-${SX_SDK_VERSION}/"

LIC_FILES_CHKSUM = " \
    file://${S}/License.txt;md5=801f80980d171dd6425610833a22dbe6;  \
"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${prefix}/bin/

    cp -R ${S}/usr/bin/dvs_stop.sh ${D}${prefix}/bin/
    chmod -R 755 ${D}${prefix}/bin/
}

FILES_${PN} += "${prefix}/bin/"
