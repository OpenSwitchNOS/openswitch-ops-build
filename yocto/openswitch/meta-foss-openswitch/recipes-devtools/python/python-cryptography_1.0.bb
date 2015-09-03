SUMMARY = "Python-pam makes the PAM (Pluggable Authentication Modules) functions available in Python.       \
           With this module you can write Python applications that implement authentication services using PAM"
HOMEPAGE = "https://pypi.python.org/pypi/cryptography"

SECTION = "devel/python"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRCNAME = "cryptography"
DEPENDS = "python-cffi"
RDEPENDS_${PN} = "python-cffi"
SRC_URI[md5sum] = "3f2608eb94dcc6e616c3cc2e182181b0"
SRC_URI[sha256sum] = "211c02fe77d791d7fc437227ba1c046268d5da665e05d8a53fc19f4f74c21001"
S = "${WORKDIR}/${SRCNAME}-${PV}"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"

inherit pypi 

CLEANBROKEN = "1"
