SUMMARY = "Network Time Protocol Implementation"
HOMEPAGE = "https://gitlab.com/NTPsec/ntpsec"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS = "python-dev glibc bison-native ncurses libevent libcap openssl readline libedit"
PR = "r2"

SRC_URI = "gitsm://gitlab.com/NTPsec/ntpsec.git;protocol=https"
SRCREV="a24a82ad16280bb066789fd9d1edfeeea3eeba29"

#S = "${WORKDIR}/ntpsec-${PV}"

do_configure() {
  BUILD_SYS=${BUILD_SYS} HOST_SYS=${HOST_SYS};
  cp -rf ${WORKDIR}/git/* ${S}
  ./waf -v configure --notests --prefix=${D}${prefix} --libdir=${D}${libdir} --bindir=${D}${bindir}; 
}

do_compile() {
	./waf -v build ${PARALLEL_MAKE};
}

do_install() {
  ./waf install --bindir=${D}${bindir};
  install -d ${D}"${DIR_${PN}}"
}

inherit pkgconfig
