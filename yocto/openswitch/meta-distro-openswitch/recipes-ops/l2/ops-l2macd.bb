SUMMARY = "L2MACD Daemon"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS = "ops-ovsdb ops-cli"

SRC_URI = "git://git.openswitch.net/openswitch/ops-l2macd;protocol=http\
           file://ops-l2macd.service"

SRCREV = "927142bb2d8cd1f688dbeb4625ec36f2f5a2eaf6"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

FILES_${PN} += "/usr/lib/cli/plugins/"

inherit openswitch cmake
