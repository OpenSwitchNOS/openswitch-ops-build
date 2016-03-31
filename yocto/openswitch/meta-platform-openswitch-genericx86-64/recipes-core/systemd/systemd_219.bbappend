# Copyright (C) 2016 Hewlett Packard Enterprise Development LP

# On generic X86 build (which runs on virtual platforms),
# serial consoles are absent. There is no need to run
# getty on those platforms.
#

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += " file://ops_core_system.conf \
             file://ops_core_profile \
             file://50-default.conf "

# systemd 50-default.conf changes sysctl kernel.core_pattern .
# Overwriting 50-default.conf with empty file ensures that sysctl
# kernel.core_pattern will not change . We don't want to change for docker .

do_install_append() {
    /bin/rm -rf ${D}/${sysconfdir}/systemd/system/getty.target.wants/getty@tty1.service
    /bin/rm -rf ${D}/${sysconfdir}/systemd/system/getty.target.wants
    /bin/rm -rf ${D}/${systemd_unitdir}/system/getty@.service

    install -d          ${D}${libdir}/sysctl.d
    install -d          ${D}${sysconfdir}/systemd
    install -d          ${D}${sysconfdir}/profile.d

    install -c -m 755   ${WORKDIR}/ops_core_profile       ${D}${sysconfdir}/profile.d/ops_core_profile
    install -c -m 755   ${WORKDIR}/ops_core_system.conf   ${D}${sysconfdir}/systemd/ops_core_system.conf
    install -c -m 755   ${WORKDIR}/50-default.conf        ${D}${libdir}/sysctl.d/50-coredump.conf
}

FILES_${PN} += " ${sysconfdir}/profile.d ${sysconfdir}/systemd ${libdir}/sysctl.d "
