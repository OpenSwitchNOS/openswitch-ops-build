SUMMARY = "OpenSwitch Temperature Daemon"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS = "ops-hw-config ops-ovsdb ops-cli ops-supportability lm-sensors"

BRANCH ?= "${OPS_REPO_BRANCH}"

SRC_URI = "${OPS_REPO_BASE_URL}/ops-tempd;protocol=${OPS_REPO_PROTOCOL};branch=${BRANCH} \
           file://ops-tempd.service \
"

SRCREV = "b63592dfaaca819b0f879f6765563fc96d2e1629"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

do_install_append() {
     install -d ${D}${systemd_unitdir}/system
     install -m 0644 ${WORKDIR}/ops-tempd.service ${D}${systemd_unitdir}/system/
}

PLATFORM_TYPE .= "${@bb.utils.contains('MACHINE_FEATURES','ops-tempd-sysfs','libtempd-sysfs-plugin','',d)}"
PLATFORM_TYPE .= "${@bb.utils.contains('MACHINE_FEATURES','ops-tempd-i2c','libtempd-i2c-plugin','',d)}"

EXTRA_OECMAKE += "-DLIBDIR=${libdir}"
EXTRA_OECMAKE += "-DPLATFORM_TYPE_STR=${PLATFORM_TYPE}"

FILES_${PN} += "${libdir}/platform/plugins"

FILES_${PN} += "/usr/lib/cli/plugins/"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "ops-tempd.service"

inherit openswitch cmake systemd
