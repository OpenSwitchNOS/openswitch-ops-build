do_install_append() {
    echo "DumpCore=yes" >>  ${D}/etc/systemd/system.conf
    echo "DefaultLimitCORE=infinity" >>  ${D}/etc/systemd/system.conf
}
