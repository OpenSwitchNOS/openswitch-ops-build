SUMMARY = "BroadView for OpenSwitch"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS = "ops-openvswitch ops-ovsdb ops-cli"

BRANCH ?= "${OPS_REPO_BRANCH}"

SRC_URI = "${OPS_REPO_BASE_URL}/ops-broadview;protocol=${OPS_REPO_PROTOCOL};branch=${BRANCH}"

SRCREV="687ab37e8019d81e772600e2b1121be082460aeb"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
#PV = "git${SRCPV}"

S = "${WORKDIR}/git"

export  BV_OVS_INCLUDE="${STAGING_DIR_TARGET}/usr/include/ovs"
export  BV_OUTPUT="${S}/output/deliverables"
export  BV_TARGET_SYSROOT="${STAGING_DIR_TARGET}"
CFLAGS += "--sysroot=${STAGING_DIR_TARGET}"
do_compile () {
    export CROSS_COMPILE="${TARGET_PREFIX}"
    make
}

do_install() {
    # Installing executable
    install -d ${D}/usr/bin
    install -m 0755 ${S}/output/deliverables/BroadViewAgent ${D}/usr/bin/ops-broadview
    install -d ${D}${sysconfdir}
    install -m 0644 ${S}/output/deliverables/broadview_config.cfg ${D}${sysconfdir}/
    install -m 0644 ${S}/output/deliverables/broadview_ovsdb_config.cfg ${D}${sysconfdir}/
}
inherit openswitch
FILES_${PN} +="$(sysconfdir)/ "

