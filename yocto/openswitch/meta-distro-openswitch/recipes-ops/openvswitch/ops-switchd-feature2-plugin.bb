SUMMARY = "OpenSwitch OVS Simulator plugin"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS = "ops-ovsdb"

SRC_URI = "git://git@github-sc-p.corp.hp.com/hpe-networking/feature2-plugin.git;protocol=ssh \
"
FILES_${PN} = "${libdir}/openvswitch/plugins ${includedir}/*"
FILES_${PN}-test = "${bindir}/plugins/*"
FILES_${PN}-doc = "${docdir}"
PACKAGES = "${PN}-dbg ${PN}-test ${PN} ${PN}-doc ${PN}-dev ${PN}-locale"

SRCREV = "${AUTOREV}"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

do_install () {
    install -d ${D}${bindir}/plugins
}

inherit openswitch cmake
