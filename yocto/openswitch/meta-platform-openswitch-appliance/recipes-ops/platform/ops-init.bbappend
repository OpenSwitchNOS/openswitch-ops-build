# Copyright (C) 2016 Hewlett Packard Enterprise Development LP

PR_append = "_appliance"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

do_install_append() {
    if ${@bb.utils.contains('IMAGE_FEATURES','ops-p4','true','false',d)}; then
        sed -i -e '4i/sbin/ip netns add emulns ; /sbin/ip netns exec emulns /sbin/ifconfig lo up' ${D}${sbindir}/ops-init
        sed -i -e 's/netns swns/netns emulns/g' ${D}${sbindir}/ops-init
    fi
}
