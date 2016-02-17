require qemu.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=441c28d2cf86e15a37fa47e15a72fbac \
                    file://COPYING.LIB;endline=24;md5=c04def7ae38850e7d3ef548588159913"

SRC_URI += "file://vmdk-streamOptimized.patch"

SRC_URI_prepend = "http://wiki.qemu-project.org/download/${BP}.tar.bz2"
SRC_URI[md5sum] = "f469f2330bbe76e3e39db10e9ac4f8db"
SRC_URI[sha256sum] = "3443887401619fe33bfa5d900a4f2d6a79425ae2b7e43d5b8c36eb7a683772d4"

COMPATIBLE_HOST_class-target_mips64 = "null"

do_install_append() {
    # Prevent QA warnings about installed ${localstatedir}/run
    if [ -d ${D}${localstatedir}/run ]; then rmdir ${D}${localstatedir}/run; fi
}
