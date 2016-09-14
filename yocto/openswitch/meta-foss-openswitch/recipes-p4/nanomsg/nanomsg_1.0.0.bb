SUMMARY = "Socket library that provides several common communication patterns"
DESCRIPTION = "\
nanomsg is a socket library that provides several common communication \
patterns. It aims to make the networking layer fast, scalable, and easy \
to use. Implemented in C, it works on a wide range of operating systems \
with no further dependencies. \
"
HOMEPAGE = "http://nanomsg.org/"
SECTION = "libs"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://COPYING;md5=587b3fd7fd291e418ff4d2b8f3904755"

SRC_URI = "https://github.com/${BPN}/${BPN}/archive/${PV}.tar.gz \
"

SRC_URI[md5sum] = "6f56ef28c93cee644e8c4aaaef7cfb55"
SRC_URI[sha256sum] = "24afdeb71b2e362e8a003a7ecc906e1b84fd9f56ce15ec567481d1bb33132cc7"

inherit cmake

DEPENDS = " \
	libtool-cross \
"

LIBTOOL = "${B}/${HOST_SYS}-libtool"
EXTRA_OEMAKE = "'LIBTOOL=${LIBTOOL}'"

DEPENDS_class-native = "libtool-native"
EXTRA_OEMAKE_class-native = "LIBTOOL=${BUILD_SYS}-libtool"

BBCLASSEXTEND = "native"
