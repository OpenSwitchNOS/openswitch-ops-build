DESCRIPTION = "OpenSwitch coredump utility"
LICENSE = "GPL-3.0 & MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891\
                    file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://ops_coredump \
           file://coredump.service \
           file://ops_cdm.sh \
           file://ops_corefile.conf \
           file://ops_coredump_sysctl.conf \
           file://ops_core_system.conf \
           file://ops_core_profile"

S = "${WORKDIR}"

do_install() {
    install -d          ${D}${systemd_unitdir}/system
    install -d          ${D}${base_sbindir}
    install -d          ${D}/tmp
    install -d          ${D}${bindir}
    install -d          ${D}${sysconfdir}/systemd
    install -d          ${D}${sysconfdir}/sysctl.d
    install -d          ${D}${sysconfdir}/profile.d

    install -c -m 755   ${WORKDIR}/ops_coredump              ${D}${bindir}/coredump
    install -c -m 755   ${WORKDIR}/ops_cdm.sh                ${D}${base_sbindir}/ops_cdm.sh
    install -c -m 755   ${WORKDIR}/ops_corefile.conf         ${D}${sysconfdir}/ops_corefile.conf
    install -c -m 0644  ${WORKDIR}/coredump.service          ${D}${systemd_unitdir}/system/coredump.service
    install -c -m 755   ${WORKDIR}/ops_cdm.sh                ${D}/tmp/ops_cdm.sh
    install -c -m 755   ${WORKDIR}/ops_corefile.conf         ${D}/tmp/ops_corefile.conf
    install -c -m 755   ${WORKDIR}/ops_coredump_sysctl.conf  ${D}${sysconfdir}/sysctl.d/ops_coredump_sysctl.conf
    install -c -m 755   ${WORKDIR}/ops_core_system.conf      ${D}${sysconfdir}/systemd/ops_core_system.conf
    install -c -m 755   ${WORKDIR}/ops_core_profile          ${D}${sysconfdir}/profile.d/ops_core_profile
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "coredump.service"
inherit openswitch systemd
FILES_${PN} += "${sysconfdir}/systemd/system /tmp ${sysconfdir}/sysctl.d ${sysconfdir}/profile.d ${bindir} "
