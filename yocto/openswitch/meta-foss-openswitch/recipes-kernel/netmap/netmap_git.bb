DESCRIPTION = "netmap - very fast packet I/O from userspace (FreeBSD/Linux)"
SECTION = "BSP"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

PR = "r0"
PV = "git"

SRCREV="30da094081157b2318d94783940435bdba062b71"

SRC_URI = " \
    git://github.com/luigirizzo/netmap.git;protocol=https \
    file://add-modules-install-target.patch \
"

S = "${WORKDIR}/git/LINUX"
B = "${S}"

do_configure() {
	./configure \
            --kernel-dir=${STAGING_KERNEL_BUILDDIR} \
            --no-drivers --disable-vale 
}

inherit module
