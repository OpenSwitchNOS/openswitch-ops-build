SUMMARY = "Library of OpenSwitch utils"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS = "ops-openvswitch"

SRC_URI = "git://git-nos.rose.rdlabs.hpecorp.net/openswitch/ops-utils;protocol=https;branch=rel/dill"

SRCREV = "79c370f0d7b521d29ac123b0cc7f937918397141"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

inherit openswitch cmake
