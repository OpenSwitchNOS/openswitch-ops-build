inherit kernel
require linux.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

KERNEL_RELEASE = "4.5"
S = "${WORKDIR}/linux-4.5"

PR = "r1"
PV = "${KERNEL_RELEASE}"

SRC_URI = "https://cdn.kernel.org/pub/linux/kernel/v4.x/linux-4.5.tar.xz;name=kernel \
"

SRC_URI[kernel.md5sum] = "a60d48eee08ec0536d5efb17ca819aef"
SRC_URI[kernel.sha256sum] = "a40defb401e01b37d6b8c8ad5c1bbab665be6ac6310cdeed59950c96b31a519c"

do_install_append() {
   #remove empty directories to avoid errors during packaging
   find ${D}/lib/modules -empty | xargs rm -rf
}

# Disabling this logic since it's not sstate aware and therefore triggers
# unrequired builds and slows CI
#
# do_import_dts() {
#   if test "${ARCH}" = "powerpc" ; then
#      if test -n "${PLATFORM_DTS_FILE}" ; then
#         echo "Updating in-kernel dts file with ${PLATFORM_DTS_FILE}"
#         cp ${PLATFORM_DTS_FILE} ${S}/arch/powerpc/boot/dts/
#      fi
#   fi
#}
#
#addtask do_import_dts after do_patch before do_compile
