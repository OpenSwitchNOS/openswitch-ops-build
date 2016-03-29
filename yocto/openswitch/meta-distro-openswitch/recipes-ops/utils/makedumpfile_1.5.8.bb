DESCRIPTION = "Make dump file utility"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"
SRC_URI = "${SOURCEFORGE_MIRROR}/projects/makedumpfile/files/makedumpfile/${PV}/makedumpfile-${PV}.tar.gz;name=makedumpfile"

SRC_URI[makedumpfile.md5sum] = "642d975349dff744c6027d4486499258"
SRC_URI[makedumpfile.sha256sum] = "dd9c6c40c1ae6774b61bbe7b53f5ebbee9734f576d8ecb75ffb929288f5ea64d"

DEPENDS = "zlib elfutils bzip2"

EXTRA_OEMAKE = "TARGET=${TARGET_ARCH}"

do_install() {
   install -d ${D}${bindir}/
   install -c -m 755 ${S}/makedumpfile ${D}${bindir}/
}
