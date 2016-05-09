SUMMARY = "OpenSwitch"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

BRANCH ?= "${OPS_REPO_BRANCH}"

SRC_URI = "${OPS_REPO_BASE_URL}/ops;protocol=${OPS_REPO_PROTOCOL};branch=${BRANCH}"

SRCREV = "2308bfb19c586aeed6f5c231737d1e1bc01d60ac"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

FILES_${PN} = "/usr/share/openvswitch/ /usr/share/openvswitch/*.extschema /usr/share/openvswitch/*.xml /usr/share/openvswitch/*.ovsschema"

OPS_SCHEMA_PATH="${S}/schema"

EXTRA_OEMAKE = "-e TOPDIR=${TOPDIR} BUILD_ROOT=${BUILD_ROOT} MACHINE=${MACHINE} PYTHON=${PYTHON}"
do_compile_prepend() {
  rm -rf ${BUILD_ROOT}/images/image_features.${MACHINE} ${BUILD_ROOT}/images/image_features
  echo ${IMAGE_FEATURES} > ${BUILD_ROOT}/images/image_features.${MACHINE}
  ln -s ${BUILD_ROOT}/images/image_features.${MACHINE} ${BUILD_ROOT}/images/image_features
}

do_install() {
	oe_runmake install DESTDIR=${D} PREFIX=${prefix}
}
