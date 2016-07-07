DESCRIPTION = "OpenSwitch daemon coredump"
LICENSE = "GPL-3.0 & MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891\
                    file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
    file://ops-coredump-system.conf \
    file://ops-coredump-sysctl.conf \
    file://ops-coredump-limits.conf \
    file://ops-coredump-profile \
"

S = "${WORKDIR}"

do_install() {
    install -d ${D}/etc/systemd/system.conf.d
    install -d ${D}/etc/sysctl.d
    install -d ${D}/etc/security/limits.d
    install -d ${D}/etc/profile.d
    install -c -m 0644  ${S}/ops-coredump-system.conf ${D}/etc/systemd/system.conf.d/ops-coredump-system.conf
    install -c -m 0644  ${S}/ops-coredump-sysctl.conf ${D}/etc/sysctl.d/ops-coredump-sysctl.conf
    install -c -m 0644  ${S}/ops-coredump-limits.conf ${D}/etc/security/limits.d/ops-coredump-limits.conf
    install -c -m 0755  ${S}/ops-coredump-profile     ${D}/etc/profile.d/ops-coredump-profile
}

FILES_${PN} += "\
    /etc/systemd/system.conf.d \
    /etc/sysctl.d  \
    /etc/security/limits.d \
    /etc/profile.d \
"
