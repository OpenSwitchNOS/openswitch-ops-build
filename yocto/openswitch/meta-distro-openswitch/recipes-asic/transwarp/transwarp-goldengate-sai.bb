SUMMARY = "Centec SAI to program GoldenGate"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

DEPENDS_${PN} += "systemd"

PROVIDES = "virtual/sai"
RPROVIDES_${PN} = "virtual/sai"

SRC_URI = "git://github.com/Centecnetworks/goldengate-sai;protocol=https"

SRCREV = "083d023d83e7370456e31cadd9652ebe9b6515ef"

inherit module-base
inherit kernel-module-split

addtask make_scripts after do_patch before do_compile
do_make_scripts[lockfiles] = "${TMPDIR}/kernel-scripts.lock"
do_make_scripts[depends] += "virtual/kernel:do_shared_workdir"
# add all splitted modules to PN RDEPENDS
KERNEL_MODULES_META_PACKAGE = "${PN}"

EXTRA_OEMAKE += "KERNEL_SRC=${STAGING_KERNEL_DIR}"

# When using AUTOREV, we need to force the package version
# to the revision of git in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"
EXTERNALSRC_BUILD??="${S}/output"

# Avoid running make clean during configuration stage
CLEANBROKEN = "1"

do_compile() {
    # Compile kernel modules
    cd ${S}/dal
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
    oe_runmake KERNEL_PATH=${STAGING_KERNEL_DIR} KERNEL_VERSION=${KERNEL_VERSION} \
        CC="${KERNEL_CC}" LD="${KERNEL_LD}" AR="${KERNEL_AR}" \
        O=${STAGING_KERNEL_BUILDDIR} ${MAKE_TARGETS}
}

do_compile[depends] += "virtual/kernel:do_shared_workdir"

do_install() {
    # Installing headers
    install -d ${D}${includedir}
    cp -Rp ${S}/include/* ${D}${includedir}

    # Installing library
    install -d ${D}${libdir}/pkgconfig
    install -m 0755 ${S}/lib/libsai.so.1.0.0 ${D}${libdir}
    ln -s libsai.so.1.0.0 ${D}${libdir}/libsai.so
    install -m 0655 ${S}/lib/sai.pc ${D}${libdir}/pkgconfig
}

INSANE_SKIP_${PN} += "already-stripped"
INSANE_SKIP_${PN} += "ldflags"

inherit openswitch
