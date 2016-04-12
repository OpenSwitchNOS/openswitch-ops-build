SUMMARY = "Library for configuration from yaml"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS = "yaml-cpp gtest i2c-tools"

SRC_URI = "git://git.openswitch.net/openswitch/ops-config-yaml;protocol=https"

SRCREV = "9916c9151ec69fda1af80acc060c1721a0f2e385"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

inherit openswitch
