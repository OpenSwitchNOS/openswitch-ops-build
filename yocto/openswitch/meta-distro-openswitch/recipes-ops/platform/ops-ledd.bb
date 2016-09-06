SUMMARY = "OpenSwitch LED Daemon"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS = "ops-hw-config ops-ovsdb ops-cli ops-supportability lm-sensors"

BRANCH ?= "${OPS_REPO_BRANCH}"

SRC_URI = "${OPS_REPO_BASE_URL}/ops-ledd;protocol=${OPS_REPO_PROTOCOL};branch=${BRANCH} \
           file://ops-ledd.service \
"

SRCREV = "c956dc07557911114a1ec528539e0b2b1e2276d6"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

do_install_append() {
     install -d ${D}${systemd_unitdir}/system
     install -m 0644 ${WORKDIR}/ops-ledd.service ${D}${systemd_unitdir}/system/
}

# We have to choose only one platform type until ops-sysd does't support multiple platforms
PLATFORM_TYPE .= "${@bb.utils.contains('MACHINE_FEATURES','ops-ledd-sysfs','libledd-sysfs-plugin','',d)}"
PLATFORM_TYPE .= "${@bb.utils.contains('MACHINE_FEATURES','ops-ledd-i2c','libledd-i2c-plugin','',d)}"

EXTRA_OECMAKE += "-DLIBDIR=${libdir}"
EXTRA_OECMAKE += "-DPLATFORM_TYPE_STR=${PLATFORM_TYPE}"

FILES_${PN} += "${libdir}/cli/plugins/ ${libdir}/platform/plugins"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "ops-ledd.service"

inherit openswitch cmake systemd
