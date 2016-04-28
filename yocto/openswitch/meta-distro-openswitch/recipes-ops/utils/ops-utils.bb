SUMMARY = "Library of OpenSwitch utils"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS = "ops-openvswitch"

SRC_URI = "${OPS_REPO_BASE_URL}/ops-utils;protocol=${OPS_REPO_PROTOCOL};branch=${OPS_REPO_BRANCH}"

SRCREV = "04d1b9fab6d8a897ad21fc1444ff75cf2d37e2d6"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

inherit openswitch cmake
