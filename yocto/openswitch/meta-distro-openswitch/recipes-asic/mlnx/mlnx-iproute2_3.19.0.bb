# Copyright Mellanox Technologies, Ltd. 2001-2016.

SUMMARY = "Mellanox SX iproute2 utilities"
LICENSE = "GPLv2+"

LIC_FILES_CHKSUM = " \
    file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6;  \
"

inherit update-alternatives openswitch

PROVIDES += "iproute2"
RPROVIDES_${PN} += "iproute2"

RDEPENDS_${PN} += "bash iptables"

SX_SDK_VERSION ??= ""
SX_SDK_URI ??= "file://"

SRC_URI = " \
    ${SX_SDK_URI}/mlnx-iproute2-3.19.0-${SX_SDK_VERSION}.tar.gz;subdir=mlnx-iproute2-3.19.0-${SX_SDK_VERSION} \
"

SRC_URI[md5sum] = "c112006e6f6937c76b1ddf09c9ae0293"
SRC_URI[sha256sum] = "5a2a93581ecdf913b1e90d80c754858289121aed8e26741fe7d4801918343d95"

S = "${WORKDIR}/mlnx-iproute2-3.19.0-${SX_SDK_VERSION}"

# Skip the unwanted steps
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${sysconfdir}/
    install -d ${D}${base_sbindir}/
    install -d ${D}${libdir}/
    install -d ${D}${localstatedir}/

    cp -R ${S}/etc/* ${D}${sysconfdir}/
    chmod -R 755 ${D}${sysconfdir}/

    cp -R ${S}/sbin/* ${D}${base_sbindir}/
    chmod -R 755 ${D}${base_sbindir}/

    cp -R ${S}/usr/lib/* ${D}${libdir}/
    chmod -R 755 ${D}${libdir}/

    cp -R ${S}/var/* ${D}${localstatedir}/
    chmod -R 755 ${D}${localstatedir}/
}

# There are only .so files in iproute2
INSANE_SKIP_${PN} = "dev-so"
# Avoid QA Issue: already-stripped
INSANE_SKIP_${PN} += "already-stripped"

FILES_${PN} += "${sysconfdir} ${base_sbindir}/ ${libdir} ${localstatedir}"

ALTERNATIVE_${PN} = "ip"
ALTERNATIVE_TARGET[ip] = "${base_sbindir}/ip.iproute2"
ALTERNATIVE_LINK_NAME[ip] = "${base_sbindir}/ip"
ALTERNATIVE_PRIORITY = "100"
