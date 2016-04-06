# Copyright Mellanox Technologies, Ltd. 2001-2016.
# This software product is licensed under Apache version 2, as detailed in
# the COPYING file.

SUMMARY = "OpenSwitch SAI plugin"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS = "ops-ovsdb virtual/sai"
RDEPENDS_${PN} = "virtual/sai"

PROVIDES += "virtual/ops-switchd-switch-api-plugin"
RPROVIDES_${PN} += "virtual/ops-switchd-switch-api-plugin"

SRC_URI = " \
   git://git.openswitch.net/openswitch/ops-switchd-sai-plugin;protocol=https;branch=master \
"
FILES_${PN} = "${libdir}/openvswitch/plugins"

SRCREV = "${AUTOREV}"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

inherit openswitch cmake
