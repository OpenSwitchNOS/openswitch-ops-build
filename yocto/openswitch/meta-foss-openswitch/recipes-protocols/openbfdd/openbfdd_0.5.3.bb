SUMMARY = "An open source implementation of the BFD protocol"
HOMEPAGE = "https://github.com/dyninc/OpenBFDD"
LICENSE = "BSD"

LIC_FILES_CHKSUM = "file://LICENSE;md5=69966e3881d50a528d1e93dd31615502"

SRC_URI = "git://github.com/dyninc/OpenBFDD.git;protocol=https"

SRCREV = "895cfb523bb96b3ef199fc5916578482ccd528ee"

S = "${WORKDIR}/git"

inherit autotools
