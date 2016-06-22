SUMMARY = "Python code coverage library"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cbc4e25353c748c817db2daffe605e43"

SRC_URI = "https://pypi.python.org/packages/39/07/bdd2d985ae7ac726cc5e7a6a343b585570bf1f9f7cb297a9cd58a60c7c89/pytest-cov-2.2.1.tar.gz"

SRC_URI[md5sum] = "d4c65c5561343e6c6a583d5fd29e6a63"
SRC_URI[sha256sum] = "a8b22e53e7f3b971454c35df99dffe21f4749f539491e935c55d3ff7e1b284fa"

RDEPENDS_${PN} = "python-py"
DEPENDS_class-native = "python-py-native"

inherit pypi
