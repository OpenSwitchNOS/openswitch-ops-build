SUMMARY = "Network Time Protocol Implementation"
HOMEPAGE = "https://gitlab.com/NTPsec/ntpsec"
SECTION = "net"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS = "python-dev glibc bison-native ncurses libevent libcap openssl readline libedit"

SRC_URI = "gitsm://gitlab.com/NTPsec/ntpsec.git;protocol=https \
           file://ntpd.service \
           file://ntp.conf \
           file://ntp.keys \
"

SRCREV="73f0adacf1a7412a382fe936c59ac0155015a94b"

S = "${WORKDIR}/git"

inherit waf pkgconfig systemd

do_install_append () {
    install -d ${D}${sysconfdir}/ ${D}${systemd_unitdir}/system ${D}${sysconfdir}/ntp ${D}${sysconfdir}/ntp/keys
    install -m 0644 ${WORKDIR}/ntpd.service ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/ntp.conf ${D}${sysconfdir}/ntp.conf
    install -m 0644 ${WORKDIR}/ntp.keys ${D}${sysconfdir}/ntp.keys
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "ntpd.service"
