SUMMARY = "OpenSwitch HW-Vtep Daemon"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS = "ops-ovsdb"

BRANCH ?= "${OPS_REPO_BRANCH}"

SRC_URI = "${OPS_REPO_BASE_URL}/ops-hw-vtep;protocol=${OPS_REPO_PROTOCOL};branch=${BRANCH} \
           file://ops-hw-vtep.service"

SRCREV = "ad5665cb25f6987899c4bd822e69794349a28ba1"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.

PV = "git${SRCPV}"

S = "${WORKDIR}/git"

do_install_append() {
     install -d ${D}${systemd_unitdir}/system
     install -m 0644 ${WORKDIR}/ops-hw-vtep.service ${D}${systemd_unitdir}/system/
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "ops-hw-vtep.service"

inherit openswitch cmake systemd
