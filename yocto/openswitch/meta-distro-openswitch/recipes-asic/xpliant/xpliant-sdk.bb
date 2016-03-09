SUMMARY = "Cavium Open APIs to program XPliant Switch"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=e3fc50a88d0a364313df4b21ef20c29e"

DEPENDS_${PN} += "ops-utils systemd"

DEPENDS = "libxml2 libpcap lmsensors"

PROVIDES = "${PACKAGES}"

SRC_URI = "git://github.com/xpliant/OpenXPS;protocol=http"

PACKAGES = "${PN}"

FILES_${PN} = "\
    ${libdir}/libOpenXps.so \
"
SRCREV = "${AUTOREV}"

# When using AUTOREV, we need to force the package version
# to the revision of git in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

INHIBIT_PACKAGE_STRIP = "1"

# Avoid running make clean during configuration stage
CLEANBROKEN = "1"

do_install() {
    # Installing headers
    install -d ${D}/${includedir}
    cp -Rp ${S}/include/* ${D}/${includedir}

    # Installing library
    install -d ${D}/usr/lib/pkgconfig
    install -m 0755 ${S}/lib/libOpenXps.so ${D}/${libdir}/
    install -m 0655 ${S}/lib/libOpenXps.pc ${D}/${libdir}/pkgconfig/
}

INSANE_SKIP_${PN} = "installed-vs-shipped"

inherit openswitch
