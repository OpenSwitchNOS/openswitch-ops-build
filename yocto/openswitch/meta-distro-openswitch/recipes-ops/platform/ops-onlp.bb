SUMMARY = "ONLP lib"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/opennetworklinux/ONL.git;protocol=http"
SRCREV = "${AUTOREV}"

do_compile () {
    make 'ONL=${S}' 'GCC=${CC} ${CFLAGS} -DONLPLIB_CONFIG_I2C_USE_CUSTOM_HEADER=0' -C  ${S}/components/amd64/onlp/onlp  deb
    make 'ONL=${S}' 'GCC=${CC} ${CFLAGS} -DONLPLIB_CONFIG_I2C_USE_CUSTOM_HEADER=0' -C  ${S}/components/amd64/onlp/onlp-platform-defaults  deb
    make 'ONL=${S}' 'GCC=${CC} ${CFLAGS} -DONLPLIB_CONFIG_I2C_USE_CUSTOM_HEADER=0' -C  ${S}/components/amd64/onlp/onlp-x86-64-quanta-ly8-rangeley-r0 deb
    #make 'ONL=${S}' 'GCC=${CC} ${CFLAGS} -DONLPLIB_CONFIG_I2C_USE_CUSTOM_HEADER=0' -C  ${S}/components/amd64/onlp/onlp-x86-64-accton-as5712-54x-r0 deb
}
inherit openswitch
