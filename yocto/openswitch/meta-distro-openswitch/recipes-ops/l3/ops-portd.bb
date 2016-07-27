SUMMARY = "OpenSwitch Port Manager Daemon"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS = "ops-utils ops-ovsdb ops-supportability"

SRC_URI = "git://git.openswitch.net/openswitch/ops-portd;protocol=http;branch=rel/dill \
           file://ops-portd.service \
           "

SRCREV = "0341f945add78a4bdea20d899a2443824ea772c0"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"
FILES_${PN} += "/usr/share/opsplugins "
do_install_append() {
     install -d ${D}${systemd_unitdir}/system
     install -m 0644 ${WORKDIR}/ops-portd.service ${D}${systemd_unitdir}/system/
     install -d ${D}/usr/share/opsplugins
     for plugin in $(find ${S}/ops/opsplugins -name "*.py"); do \
         install -m 0644 ${plugin} ${D}/usr/share/opsplugins
     done
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "ops-portd.service"

inherit openswitch cmake systemd
