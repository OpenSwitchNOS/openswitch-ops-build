# Copyright (C) 2016 Hewlett Packard Enterprise Development LP

# systemd 50-default.conf changes sysctl kernel.core_pattern .
# Removing 50-default.conf will ensure sysctl kernel.core_pattern will
# remain unchanged .

do_install_append() {
    # Move login screen to boot console
    mv ${D}/${sysconfdir}/systemd/system/getty.target.wants/getty@tty1.service \
       ${D}/${sysconfdir}/systemd/system/getty.target.wants/getty@console.service
    /bin/rm -f  ${D}${libdir}/sysctl.d/50-coredump.conf
}
