DESCRIPTION = "OpenSwitch kdump service"
LICENSE = "GPL-3.0 & MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891\
                    file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://kdump.conf \
           file://ops_kdump \
           file://kdump.service \
           file://kdump.target \
           file://ops_sysctl.conf "

RDEPENDS_${PN} = "makedumpfile kexec-tools"

S = "${WORKDIR}"

do_install() {
    install -d          ${D}${systemd_unitdir}/system
    install -d          ${D}${bindir}
    install -d          ${D}${sysconfdir}/sysctl.d

    install -c -m 755   ${WORKDIR}/kdump.conf             ${D}${sysconfdir}/kdump.conf
    install -c -m 755   ${WORKDIR}/ops_kdump              ${D}${bindir}/kdump
    install -c -m 0644  ${WORKDIR}/kdump.service          ${D}${systemd_unitdir}/system/kdump.service
    install -c -m 755   ${WORKDIR}/ops_sysctl.conf        ${D}${sysconfdir}/sysctl.d/ops_sysctl.conf
    install -c -m 755   ${WORKDIR}/kdump.target ${D}/lib/systemd/system
    install -d          ${D}${sysconfdir}/systemd/system/kdump.target.wants/
    ln -sf /lib/systemd/system/kdump.service ${D}${sysconfdir}/systemd/system/kdump.target.wants/kdump.service
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "kdump.service"
inherit openswitch systemd
FILES_${PN} += "${systemd_unitdir}/system ${bindir} ${sysconfdir}/sysctl.d "
