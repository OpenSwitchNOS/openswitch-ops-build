# Copyright Mellanox Technologies, Ltd. 2001-2016.

SUMMARY = "Hardware Management kernel drivers"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

inherit module systemd openswitch

MLNX_HW_MGMT_VERSION ??= ""
MLNX_HW_MGMT_URI ??= "file://"

SRC_URI = " \
    ${MLNX_HW_MGMT_URI}/mlnx-hw-mgmt-${LINUX_VERSION}-${MLNX_HW_MGMT_VERSION}.tar.gz;subdir=mlnx-hw-mgmt-${LINUX_VERSION}-${MLNX_HW_MGMT_VERSION} \
    file://mlnx-bsp.service \
"

SRC_URI[md5sum] = "9960eb67c8b2280c20f2791b46c76e19"
SRC_URI[sha256sum] = "f2acc065784bc103000814a16deb891d3d8c1dc618c3e1494834a69aa00715bc"

S = "${WORKDIR}/mlnx-hw-mgmt-${LINUX_VERSION}-${MLNX_HW_MGMT_VERSION}/"

# Skip the unwanted steps
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${sysconfdir}/mlnx/
    install -d ${D}${prefix}/sbin/
    install -d ${D}${prefix}/bin/
    install -d ${D}/lib/lsb/
    install -d ${D}${systemd_unitdir}/system/
    install -d ${D}/lib/modules/${KERNEL_VERSION}/extra/

    cp -R ${S}/etc/mlnx/* ${D}${sysconfdir}/mlnx/
    chmod -R 755 ${D}${sysconfdir}/mlnx/

    cp -R ${S}/usr/sbin/* ${D}${prefix}/sbin/
    chmod -R 755 ${D}${prefix}/sbin/

    cp -R ${S}/usr/bin/* ${D}${prefix}/bin/
    chmod -R 755 ${D}${prefix}/bin/

    cp -R ${S}/lib/lsb/* ${D}/lib/lsb/
    chmod -R 755 ${D}/lib/lsb/

    install -m 0644 ${WORKDIR}/mlnx-bsp.service ${D}${systemd_unitdir}/system

    cp -R ${S}/lib/modules/${LINUX_VERSION}/extra/* ${D}/lib/modules/${LINUX_VERSION}/extra/
    chmod -R 755 ${D}/lib/modules/${LINUX_VERSION}/extra/
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "mlnx-bsp.service"

FILES_${PN} = "${sysconfdir}/mlnx/ ${prefix}/sbin/ ${prefix}/bin/ /lib/lsb ${systemd_unitdir}/system/mlnx-bsp.service"
