SUMMARY = "OpenSwitch LACP Daemon"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS = "ops-ovsdb ops-utils ops-cli"

SRC_URI = "git://git.openswitch.net/openswitch/ops-lacpd;protocol=http\
           file://ops-lacpd.service \
"

SRCREV = "17c074788689bbd4aec65f6b428d0f9ac3c82af8"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

do_install_append() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/ops-lacpd.service ${D}${systemd_unitdir}/system/
}

FILES_${PN} += "/usr/lib/cli/plugins"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "ops-lacpd.service"

inherit openswitch cmake systemd
