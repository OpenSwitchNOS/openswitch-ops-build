# Copyright (C) 2015 Hewlett Packard Enterprise Development LP

PR_append = "_openswitch"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

ALTERNATIVE_${PN} += "systemd-def-target"

ALTERNATIVE_TARGET[systemd-def-target] = "${systemd_unitdir}/system/multi-user.target"
ALTERNATIVE_LINK_NAME[systemd-def-target] = "${systemd_unitdir}/system/default.target"
ALTERNATIVE_PRIORITY[systemd-def-target] ?= "1"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://systemctl-alias.sh \
    file://system.conf \
"

# We remove the dependency from os-release to avoid rebuilding constantly
RRECOMMENDS_${PN}_remove = "os-release"

FILES_${PN} += "${sysconfdir}/profile.d/"

do_install_append() {
    install -d ${D}${sysconfdir}/profile.d
    install -m 755 ${WORKDIR}/systemctl-alias.sh ${D}${sysconfdir}/profile.d/systemctl-alias.sh
    ln -s /dev/null ${D}/etc/udev/rules.d/80-net-setup-link.rules
    install -m 644 ${WORKDIR}/system.conf ${D}${sysconfdir}/systemd/
}

# We use systemd core dump
EXTRA_OECONF_remove = "--disable-coredump"
EXTRA_OECONF += "--with-dns-servers=" ""
FILES_${PN} += "${bindir}/coredumpctl"

# Enable Audit framework on OpenSwitch
PACKAGECONFIG_append += "audit"

# Make sure to only build against AppArmor if we want to
PACKAGECONFIG[apparmor] = "--enable-apparmor,--disable-apparmor,apparmor"
PACKAGECONFIG_append += "${@bb.utils.contains('DISTRO_FEATURES', 'apparmor', 'apparmor', '', d)}"
