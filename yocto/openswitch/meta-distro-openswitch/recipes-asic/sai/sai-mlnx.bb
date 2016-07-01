# Copyright Mellanox Technologies, Ltd. 2001-2016.

SUMMARY = "SAI API"
LICENSE = "Apache-2.0"
DESCRIPTION = "Mellanox SAI"

inherit autotools pkgconfig

PROVIDES += "virtual/sai"
RPROVIDES_${PN} += "virtual/sai"

DEPENDS += "libxml2 mlnx-applibs"
RDEPENDS_${PN} += "mlnx-applibs mlnx-sx-acl-rm mlnx-sx-complib mlnx-sxd-libs mlnx-sx-examples mlnx-sx-gen-utils libnl mlnx-sx-scew mlnx-sx-sdn-hal iproute2"

PREFIX = "${STAGING_DIR_HOST}/${prefix}"

EXTRA_OECONF='--with-applibs=${PREFIX} --with-sxcomplib=${PREFIX} --with-sxdlibs=${PREFIX} --with-xml2=${PREFIX}'

LIC_FILES_CHKSUM = " \
    file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6; \
"

S = "${WORKDIR}/git/mlnx_sai"
B = "${S}"

SRCREV = "0132a7172291af350f35946aeaee6911ff2710fa"

SRC_URI = " \
   git://github.com/Mellanox/SAI-Implementation.git;protocol=https;branch=openswitch \
"

FILES_${PN} += "${prefix}/share"
