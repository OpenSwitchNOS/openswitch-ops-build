# Copyright (C) 2015 Hewlett Packard Enterprise Development LP

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

PR = "1"
S = "${WORKDIR}"


SRC_URI = " \
    file://00-eth0.link \
    file://oobm.network \
"

do_install () {
   install -d ${D}${sysconfdir}/systemd/network/
   install -m 0644 00-eth0.link  ${D}${sysconfdir}/systemd/network/
   install -m 0644 oobm.network  ${D}${sysconfdir}/systemd/network/
}

FILES_${PN} = "${sysconfdir}"
