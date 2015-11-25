SUMMARY = "Mininet based Python Library for component & feature tests."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "git://git.openswitch.net/openswitch/ops-vsi;protocol=https"

SRCREV = "aa8c72142a6c5be65b68f7ee3e2a1e3e02f40a78"

# When using AUTOREV, we need to force the package version
# to the revision of git in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

inherit setuptools

RDEPENDS_${PN} = "mininet python-pytest"
DEPENDS_class-native = "mininet-native python-pytest-native python-pytest-timeout-native \
                        python-pyyaml-native util-linux-native python-smartpm-native"

BBCLASSEXTEND = "native"
