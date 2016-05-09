SUMMARY = "OpenSwitch vswitchd Broadcom plugin"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS = "ops-ovsdb virtual/opennsl ops-switchd ops-supportability ops-classifierd"

PROVIDES += "virtual/ops-switchd-switch-api-plugin"
RPROVIDES_${PN} += "virtual/ops-switchd-switch-api-plugin"

BRANCH ?= "${OPS_REPO_BRANCH}"

SRC_URI = "${OPS_REPO_BASE_URL}/ops-switchd-opennsl-plugin;protocol=${OPS_REPO_PROTOCOL};branch=${BRANCH}"

FILES_${PN} = "${libdir}/openvswitch/plugins"

SRCREV = "ed96a072896504836765848f58322bcf4fded71b"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

# Pass required feature configuration flags to make process.
# Syntax: -DENABLE_<Kconfig symbol>=1
EXTRA_OECMAKE += "${@bb.utils.contains('IMAGE_FEATURES','OPS_BUFMOND','-DENABLE_OPS_BUFMOND=1','',d)}"
EXTRA_OECMAKE += "${@bb.utils.contains('IMAGE_FEATURES','OPS_BROADVIEW','-DENABLE_OPS_BROADVIEW=1','',d)}"

# Pass required key value flags to make process.
# Syntax: -D<Kconfig symbol>=${<Kconfig symbol>}
EXTRA_OECMAKE += "-DKEY_VALUE_EXAMPLE_NUM_PORTS=${KEY_VALUE_EXAMPLE_NUM_PORTS}"

inherit openswitch cmake
