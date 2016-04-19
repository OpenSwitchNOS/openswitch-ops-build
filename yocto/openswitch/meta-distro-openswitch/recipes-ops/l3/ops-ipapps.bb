SUMMARY = "OpenSwitch IP Applications"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS = "ops-utils ops-ovsdb ops-cli"

SRC_URI = "git://git.openswitch.net/openswitch/ops-ipapps;protocol=http\
           file://ops-udpfwd.service\
           file://ops-dhcpv6r.service"

SRCREV = "367b514ec4f09e967ada36f93407fbd3bac62dcf"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

do_install_append() {
     install -d ${D}${systemd_unitdir}/system
     install -m 0644 ${WORKDIR}/ops-udpfwd.service ${D}${systemd_unitdir}/system/
     install -m 0644 ${WORKDIR}/ops-dhcpv6r.service ${D}${systemd_unitdir}/system/
}

FILES_${PN} += "/usr/lib/cli/plugins/"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "ops-udpfwd.service ops-dhcpv6r.service"

inherit openswitch cmake systemd
