do_install_append() {
    echo "ulimit -c unlimited" >>  ${D}${sysconfdir}/profile
    echo "echo 0x7f > /proc/self/coredump_filter"  >>  ${D}${sysconfdir}/profile
}
