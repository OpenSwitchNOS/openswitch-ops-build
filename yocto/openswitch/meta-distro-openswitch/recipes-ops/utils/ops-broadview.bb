SUMMARY = "BroadView for OpenSwitch"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS = "ops-openvswitch ops-ovsdb"

SRC_URI = "git://git.openswitch.net/openswitch/ops-broadview;protocol=https \
    file://ops-broadview.service \
"

SRCREV="96eeb6ae6cb24a8f876f63cd985dcb21e564a915"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
#PV = "git${SRCPV}"

S = "${WORKDIR}/ops-broadview"

export  BV_OVS_INCLUDE="${STAGING_DIR_TARGET}/usr/include/ovs"
export  BV_OUTPUT="${S}/output/deliverables"
export  BV_TARGET_SYSROOT="${STAGING_DIR_TARGET}"
CFLAGS += "--sysroot=${STAGING_DIR_TARGET}"
do_compile () {
    export CROSS_COMPILE="${TARGET_PREFIX}"
    make
}

do_install_append() {
     install -d ${D}${systemd_unitdir}/system
     install -m 0644 ${WORKDIR}/ops-broadview.service ${D}${systemd_unitdir}/system/
     install -d ${D}${sysconfdir}
     install -m 0644 ${S}/output/deliverables/broadview_config.cfg ${D}${sysconfdir}/
     install -m 0644 ${S}/output/deliverables/broadview_ovsdb_config.cfg ${D}${sysconfdir}/
}

do_install() {
    # Installing executable
    install -d ${D}/usr/bin
    install -m 0755 ${S}/output/deliverables/BroadViewAgent ${D}/usr/bin/ops-broadview

}
inherit openswitch
FILES_${PN} +="$(sysconfdir)/ ${systemd_unitdir}/system/"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "ops-broadview.service"
