DESCRIPTION = "kdump service utility"
LICENSE = "GPL-3.0 & MIT"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891\
                    file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"


SRC_URI = "file://kdump.sysconf \
file://kdump.conf \
file://functions \
file://ops_kdump \
file://kdump.service \
file://ops_cdm.sh \
file://ops_corefile.conf \
file://ops_sysctl.conf \
file://ops_core_system.conf \
file://ops_core_profile"



EXTRA_OEMAKE = "TARGET=${TARGET_ARCH}"


S = "${WORKDIR}"


do_install_append() {
    install -d        ${D}/etc/sysconfig/
    install -d        ${D}/etc/systemd/system
    install -d        ${D}/sbin
    install -d        ${D}/tmp
    install -d        ${D}/usr/lib/sysctl.d
    install -d        ${D}/usr/lib/tmpfiles.d
    install -d        ${D}/etc/profile.d

    install -c -m 755 ${WORKDIR}/kdump.sysconf          ${D}/etc/sysconfig/kdump
    install -c -m 755 ${WORKDIR}/kdump.conf             ${D}/etc/kdump.conf
    install -c -m 755 ${WORKDIR}/functions              ${D}/sbin/functions
    install -c -m 755 ${WORKDIR}/ops_kdump              ${D}/sbin/kdump
    install -c -m 755 ${WORKDIR}/ops_cdm.sh             ${D}/sbin/ops_cdm.sh

    install -c -m 755 ${WORKDIR}/ops_corefile.conf      ${D}/etc/ops_corefile.conf
    install -c -m 0644 ${WORKDIR}/kdump.service  ${D}/etc/systemd/system/kdump.service

    install -d    ${D}/etc/systemd/system/multi-user.target.wants/
    cd ${D} && ln -s   etc/systemd/system/kdump.service    etc/systemd/system/multi-user.target.wants/kdump.service && cd -

    install -c -m 755 ${WORKDIR}/ops_cdm.sh             ${D}/tmp/ops_cdm.sh
    install -c -m 755 ${WORKDIR}/ops_corefile.conf      ${D}/tmp/ops_corefile.conf
    install -c -m 755 ${WORKDIR}/ops_sysctl.conf        ${D}/usr/lib/sysctl.d/ops_sysctl.conf
    install -c -m 755 ${WORKDIR}/ops_core_system.conf   ${D}/usr/lib/tmpfiles.d/ops_core_system.conf
    install -c -m 755 ${WORKDIR}/ops_core_profile       ${D}/etc/profile.d/ops_core_profile

}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "kdump.service"
FILES_${PN}   +=  "/etc/systemd/system /tmp /usr/lib/sysctl.d /usr/lib/tmpfiles.d /etc/profile.d"
