# Copyright Mellanox Technologies, Ltd. 2001-2016.
# This software product is licensed under Apache version 2, as detailed in
# the COPYING file.

SUMMARY = "OpenSwitch SAI plugin"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit openswitch cmake

DEPENDS = "ops-ovsdb virtual/sai ops-hw-config"
RDEPENDS_${PN} = "virtual/sai"

PROVIDES += "virtual/ops-switchd-switch-api-plugin"
RPROVIDES_${PN} += "virtual/ops-switchd-switch-api-plugin"

# Change to OPS_REPO* variables when they will be available
SAI_PLUGIN_BASE_URL ?= "git:////yocto-build/git/openswitch"
SAI_PLUGIN_PROTOCOL ?= "file"
SAI_PLUGIN_BRANCH ?= "develop"

SRC_URI = " \
   ${SAI_PLUGIN_BASE_URL}/ops-switchd-sai-plugin;protocol=${SAI_PLUGIN_PROTOCOL};branch=${SAI_PLUGIN_BRANCH} \
"
FILES_${PN} = "${libdir}/openvswitch/plugins"

SRCREV = "${AUTOREV}"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"
