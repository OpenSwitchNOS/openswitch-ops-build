# Copyright Mellanox Technologies, Ltd. 2001-2016.

SUMMARY = "Switch low level drivers"
LICENSE = "BSD & GPLv2"

inherit module openswitch

SX_SDK_VERSION ??= ""
SX_SDK_URI ??= "file://"

SRC_URI = " \
    ${SX_SDK_URI}/mlnx-sx-kernel-${LINUX_VERSION}-${SX_SDK_VERSION}.tar.gz;subdir=mlnx-sx-kernel-${LINUX_VERSION}-${SX_SDK_VERSION} \
"

SRC_URI[md5sum] = "4a20b45bfd0b10d19712704b5e2fd80c"
SRC_URI[sha256sum] = "5af17de80eca3d257ee06e1909e59ca847041debc168aaf6dabbb7949b707946"

PROVIDES = "virtual/mlnx-sx-kernel"
RPROVIDES_${PN} = "virtual/mlnx-sx-kernel"

S = "${WORKDIR}/mlnx-sx-kernel-${LINUX_VERSION}-${SX_SDK_VERSION}/"

LIC_FILES_CHKSUM = " \
    file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6;  \
    file://${COMMON_LICENSE_DIR}/BSD;md5=801f80980d171dd6425610833a22dbe6;  \
"

# Skip the unwanted steps
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}/etc/init.d
    install -d ${D}/etc/modprobe.d
    install -d ${D}/etc/udev/rules.d
    install -d ${D}/lib/modules/${LINUX_VERSION}/updates/kernel/drivers/net/mlx_sx
    install -d ${D}/lib/modules/${LINUX_VERSION}/updates/kernel/drivers/net/sx_netdev

    cp -R ${S}/etc/init.d/* ${D}/etc/init.d/
    chmod -R 755 ${D}/etc/init.d/

    cp ${S}/etc/modprobe.d/* ${D}/etc/modprobe.d/
    chmod -R 755 ${D}/etc/modprobe.d/

    cp ${S}/etc/udev/rules.d/* ${D}/etc/udev/rules.d/
    chmod -R 755 ${D}/etc/udev/rules.d/

    cp ${S}/lib/modules/${LINUX_VERSION}/updates/kernel/drivers/net/mlx_sx/* ${D}/lib/modules/${LINUX_VERSION}/updates/kernel/drivers/net/mlx_sx/
    chmod -R 755 ${D}/lib/modules/${KERNEL_VERSION}/updates/kernel/drivers/net/mlx_sx/

    cp ${S}/lib/modules/${LINUX_VERSION}/updates/kernel/drivers/net/sx_netdev/* ${D}/lib/modules/${LINUX_VERSION}/updates/kernel/drivers/net/sx_netdev/
    chmod -R 755 ${D}/lib/modules/${LINUX_VERSION}/updates/kernel/drivers/net/sx_netdev/
}

FILES_${PN} += " \
    /etc/init.d/* \
    /etc/modprobe.d/* \
    /etc/udev/rules.d/* \
"
