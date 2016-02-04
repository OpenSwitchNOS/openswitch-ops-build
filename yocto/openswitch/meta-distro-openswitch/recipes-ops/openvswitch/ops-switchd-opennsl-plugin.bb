SUMMARY = "OpenSwitch vswitchd Broadcom plugin"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS = "ops-ovsdb virtual/opennsl"

PROVIDES += "virtual/ops-switchd-switch-api-plugin"
RPROVIDES_${PN} += "virtual/ops-switchd-switch-api-plugin"

SRC_URI = "git://git.openswitch.net/openswitch/ops-switchd-opennsl-plugin;protocol=http"

FILES_${PN} = "${libdir}/openvswitch/plugins"

SRCREV = "a8b8d90ca0f3d1a9723b536e5db833a422178929"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

# Pass required feature configuration flags to make process.
# Syntax: -DENABLE_<Kconfig symbol>=1
EXTRA_OECMAKE += "${@bb.utils.contains('IMAGE_FEATURES','BUFMON','-DENABLE_BUFMON=1','',d)}"
EXTRA_OECMAKE += "${@bb.utils.contains('IMAGE_FEATURES','BROADVIEW','-DENABLE_BROADVIEW=1','',d)}"

inherit openswitch cmake
