SUMMARY = "OpenSwitch"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

BRANCH ?= "${OPS_REPO_BRANCH}"

SRC_URI = "${OPS_REPO_BASE_URL}/ops;protocol=${OPS_REPO_PROTOCOL};branch=${BRANCH}"

SRCREV = "0c7976f041e5ce7eb9252c158b49320b975f9505"

DEPENDS = " \
           python-jsonref-native \
           python-pycksum-native \
          "

PYTHONPATH = "${STAGING_DIR_NATIVE}${PYTHON_SITEPACKAGES_DIR}"

export PYTHONPATH

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

FILES_${PN} += " \
                /usr/share/openvswitch/ \
                /usr/share/openvswitch/*.extschema \
                /usr/share/openvswitch/*.ovsschema \
                /usr/share/openvswitch/*.xml \
               "

OPS_SCHEMA_PATH="${S}/schema"

inherit openswitch cmake
