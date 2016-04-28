SUMMARY = "Python Library for component, feature and system level tests."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

BRANCH ?= "${OPS_REPO_BRANCH}"

SRC_URI = "${OPS_REPO_BASE_URL}/ops-topology-lib-vtysh;protocol=${OPS_REPO_PROTOCOL};branch=${BRANCH}"

SRCREV = "${AUTOREV}"

# When using AUTOREV, we need to force the package version
# to the revision of git in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

RDEPENDS_${PN} = "python-pytest"
DEPENDS_class-native = "python-pytest-native"

BBCLASSEXTEND = "native"
