SUMMARY = "OpenSwitch vswitchd Xpliant plugin"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS = "ops-ovsdb xpliant-openxps ops-switchd ops-supportability ops-classifierd"

PROVIDES += "virtual/ops-switchd-switch-api-plugin"
RPROVIDES_${PN} += "virtual/ops-switchd-switch-api-plugin"

BRANCH ?= "${OPS_REPO_BRANCH}"

SRC_URI = "${OPS_REPO_BASE_URL}/ops-switchd-xpliant-plugin;protocol=${OPS_REPO_PROTOCOL};branch=${BRANCH}"

FILES_${PN} = "${libdir}/openvswitch/plugins"

SRCREV = "73ecfffd2e20bf48c4f60c7703db71b41498c5c7"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

inherit openswitch cmake
