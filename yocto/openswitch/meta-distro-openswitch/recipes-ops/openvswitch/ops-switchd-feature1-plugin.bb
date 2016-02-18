SUMMARY = "OpenSwitch OVS Simulator plugin"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS = "ops-ovsdb ops-switchd-feature2-plugin"

SRC_URI = "git://git@github-sc-p.corp.hp.com/hpe-networking/feature1-plugin.git;protocol=ssh \
"
FILES_${PN} = "${libdir}/openvswitch/plugins ${includedir}/*"

SRCREV = "${AUTOREV}"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

inherit openswitch cmake
