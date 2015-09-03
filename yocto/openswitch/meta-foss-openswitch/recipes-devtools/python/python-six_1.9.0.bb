SUMMARY = "Python-pam makes the PAM (Pluggable Authentication Modules) functions available in Python.       \
           With this module you can write Python applications that implement authentication services using PAM"
HOMEPAGE = "https://pypi.python.org/pypi/six"

SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM="file://LICENSE;md5=6f00d4a50713fa859858dd9abaa35b21"

SRCNAME = "six"
SRC_URI[md5sum] = "476881ef4012262dfc8adc645ee786c4"
SRC_URI[sha256sum] = "e24052411fc4fbd1f672635537c3fc2330d9481b18c0317695b46259512c91d5"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit pypi 

CLEANBROKEN = "1"
