# Recipe variable overrides for feature/acl branch

SRC_URI = "git://git.openswitch.net/openswitch/ops-classifierd;protocol=http;branch=feature/acl \
           file://ops-classifierd.service \
           "

SRCREV = "d8e97c2ba9031c3446fc737af74fab527e66d8fd"


DEPENDS = "ops-hw-config ops-ovsdb ops-cli ops-switchd ops-utils audit"

RDEPENDS_${PN} = "audit"

FILES_${PN} = "${libdir}/openvswitch/plugins ${includedir}/plugins/* ${bindir} ${bindir}/ops-classifierd"

do_install_append() {
     install -d ${D}${systemd_unitdir}/system
     install -m 0644 ${WORKDIR}/ops-classifierd.service ${D}${systemd_unitdir}/system/
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "ops-classifierd.service"

FILES_${PN} += "/usr/share/opsplugins"
FILES_${PN} += "/usr/lib/cli/plugins/"

inherit openswitch autotools cmake pkgconfig systemd
