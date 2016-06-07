SUMMARY = "OpenSwitch OVS Simulator plugin"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS = "ops-ovsdb ops-switchd ops-supportability ops-classifierd"
RDEPENDS_${PN} = "openvswitch-sim-switch"

PROVIDES += "virtual/ops-switchd-switch-api-plugin"
RPROVIDES_${PN} += "virtual/ops-switchd-switch-api-plugin"

BRANCH ?= "${OPS_REPO_BRANCH}"

SRC_URI = "${OPS_REPO_BASE_URL}/ops-switchd-container-plugin;protocol=${OPS_REPO_PROTOCOL};branch=${BRANCH} \
"
FILES_${PN} = "${libdir}/openvswitch/plugins"

SRCREV = "3b0d266b7a49e558513e37ec570c1e6fa644a476"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

inherit openswitch cmake
