PR_append = "_usermode"

do_install_append() {
    # Move login screen to boot console
    mv ${D}/${sysconfdir}/systemd/system/getty.target.wants/getty@tty1.service \
       ${D}/${sysconfdir}/systemd/system/getty.target.wants/getty@console.service
}
