SUMMARY = "OpenSwitch REST API"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "git://git.openswitch.net/openswitch/ops-restapi;protocol=http \
"

SRCREV="${AUTOREV}"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"
B = "${S}"

DEPENDS = "ops-ovsdb ops-openvswitch"

SRV_DIR ?= "/srv/www"

FILES_${PN} += "/srv/www"

do_install_append() {
    install -d ${D}/srv/www/restapi
    cp -R ${S}/src/* ${D}/srv/www/restapi
    cd ${D}/${PYTHON_SITEPACKAGES_DIR}/halonlib
    PYTHONPATH=${STAGING_DIR_TARGET}/${PYTHON_SITEPACKAGES_DIR}:${PYTHONPATH} python apidocgen.py ${STAGING_DIR_TARGET}/${prefix}/share/openvswitch/vswitch.extschema ${STAGING_DIR_TARGET}/${prefix}/share/openvswitch/vswitch.xml > ${S}/src/ops-restapi.json
}
