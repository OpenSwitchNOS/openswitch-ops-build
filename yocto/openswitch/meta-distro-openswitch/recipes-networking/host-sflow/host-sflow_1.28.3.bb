SUMMARY = "Host sFlow agent"
HOMEPAGE = "http://www.sflow.net/"
# TODO - fix the license - should really be http://www.sflow.net/license.html
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3e784d8af30c680a4ddd4ddf341d9b92"

SRC_URI = "https://github.com/sflow/host-sflow/archive/v1.28.3.tar.gz"
SRC_URI[md5sum] = "3e8fe900c8991a24077c1876c4982ff8"
SRC_URI[sha256sum] = "fdfb22cd90da819eeaa6548f2aa40cf233b50bb4121bb1566f700cec6ec1b05c"


EXTRA_OEMAKE += "NFLOG=no BINDIR=${D}/usr/sbin INITDIR=${D}/etc/init.d/ CONFDIR=${D}/etc/"

inherit openswitch autotools-brokensep

do_install () {
    install -d ${D}/usr/sbin
    install -d ${D}/etc/init.d/
    install -m 0755 ${S}/src/Linux/hsflowd ${D}/usr/sbin/hsflowd
    install -m 0755 ${S}/src/Linux/scripts/hsflowd ${D}/etc/init.d/hsflowd
}

FILE_${PN} = "/usr/sbin/hsflowd /etc/init.d/hsflowd"
