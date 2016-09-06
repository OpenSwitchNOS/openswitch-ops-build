SUMMARY = "OpenSwitch Power Supply Management Daemon"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS = "ops-hw-config ops-ovsdb ops-cli ops-supportability lm-sensors"

BRANCH ?= "${OPS_REPO_BRANCH}"

SRC_URI = "${OPS_REPO_BASE_URL}/ops-powerd;protocol=${OPS_REPO_PROTOCOL};branch=${BRANCH} \
           file://ops-powerd.service \
"

SRCREV = "b8df86ddc168710409f1b345d3fe2caf7a79ecc2"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

PLATFORM_TYPE .= "${@bb.utils.contains('MACHINE_FEATURES','ops-powerd-sysfs','libpowerd-sysfs-plugin','',d)}"
PLATFORM_TYPE .= "${@bb.utils.contains('MACHINE_FEATURES','ops-powerd-i2c','libpowerd-i2c-plugin','',d)}"

EXTRA_OECMAKE += "-DLIBDIR=${libdir}"
EXTRA_OECMAKE += "-DPLATFORM_TYPE_STR=${PLATFORM_TYPE}"

do_install_append() {
     install -d ${D}${systemd_unitdir}/system
     install -m 0644 ${WORKDIR}/ops-powerd.service ${D}${systemd_unitdir}/system/
}

FILES_${PN} += "${libdir}/cli/plugins/ ${libdir}/platform/plugins"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "ops-powerd.service"

inherit openswitch cmake systemd
