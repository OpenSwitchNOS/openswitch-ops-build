SUMMARY = "OpenSwitch Pluggable Module Daemon"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS = "ops-hw-config ops-ovsdb ops-supportability"
DEPENDS += "${@bb.utils.contains('MACHINE_FEATURES','ops-pmd-mlnx','mlnx-applibs','',d)}"

BRANCH ?= "${OPS_REPO_BRANCH}"

SRC_URI = "${OPS_REPO_BASE_URL}/ops-pmd;protocol=${OPS_REPO_PROTOCOL};branch=${BRANCH} \
           file://ops-pmd.service \
"

SRCREV = "fe69e5a3a1779637c027b1df9d69d54827db220a"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

do_install_append() {
     install -d ${D}${systemd_unitdir}/system
     install -m 0644 ${WORKDIR}/ops-pmd.service ${D}${systemd_unitdir}/system/
}

PLATFORM_TYPE .= "${@bb.utils.contains('MACHINE_FEATURES','ops-pmd-mlnx','libpmd-mlnx-plugin','',d)}"
PLATFORM_TYPE .= "${@bb.utils.contains('MACHINE_FEATURES','ops-pmd-i2c','libpmd-i2c-plugin','',d)}"

EXTRA_OECMAKE += "-DLIBDIR=${libdir}"
EXTRA_OECMAKE += "-DPLATFORM_TYPE_STR=${PLATFORM_TYPE}"

FILES_${PN} += "${libdir}/platform/plugins"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "ops-pmd.service"

inherit openswitch cmake systemd
