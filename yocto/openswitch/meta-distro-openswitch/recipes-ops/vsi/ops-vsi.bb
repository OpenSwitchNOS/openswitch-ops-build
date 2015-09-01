SUMMARY = "Mininet based Python Library for component & feature tests."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://setup.py;beginline=0;endline=39;md5=9c5b9b1686974691d2cc806743d0bf3f"

SRC_URI = "git://git.openswitch.net/openswitch/ops-vsi;protocol=https"

SRCREV = "${AUTOREV}"

# When using AUTOREV, we need to force the package version
# to the revision of git in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

inherit setuptools

RDEPENDS_${PN} = "mininet python-pytest"
DEPENDS_class-native = "mininet-native python-pytest-native util-linux-native"

BBCLASSEXTEND = "native"
