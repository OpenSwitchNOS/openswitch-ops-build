SUMMARY = "Openswitch system performance tools"
DESCRIPTION = "The sysstat utilities are a collection of performance monitoring tools for Linux."
HOMEPAGE = "http://sebastien.godard.pagesperso-orange.fr/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI = "http://perso.orange.fr/sebastien.godard/sysstat-${PV}.tar.xz"
SRC_URI[md5sum] = "f823c9e3c20a38c783c7cfb946ab30dd"
SRC_URI[sha256sum] = "e92c9980c6dde7e7faa3b91eb56c82de37139dc4d07eb69ca137eed44d161f5a"

inherit openswitch
DEPENDS = "glibc"

S = "${WORKDIR}/sysstat-${PV}"

do_compile() {
    make
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/pidstat    ${D}${bindir}/pidstat
    install -m 0755 ${S}/mpstat     ${D}${bindir}/mpstat
}

FILES_${PN} += "${bindir} "
