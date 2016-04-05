DESCRIPTION = "OpenSwitch kdump service"
LICENSE = "GPL-3.0 & MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891\
                    file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://kdump.conf \
           file://kdump \
           file://kdump.service \
          "

RDEPENDS_${PN} = "makedumpfile kexec-tools"

S = "${WORKDIR}"

do_install() {
    install -d          ${D}${systemd_unitdir}/system
    install -d          ${D}${bindir}
    install -d          ${D}${sysconfdir}

    install -c -m 755   ${WORKDIR}/kdump.conf             ${D}${sysconfdir}/kdump.conf
    install -c -m 755   ${WORKDIR}/kdump                  ${D}${bindir}/kdump
    install -c -m 0644  ${WORKDIR}/kdump.service          ${D}${systemd_unitdir}/system/kdump.service
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "kdump.service"
inherit openswitch systemd
FILES_${PN} += "${systemd_unitdir}/system ${bindir} ${sysconfdir}"
