SUMMARY = "OpenSwitch Configuration Daemon"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://setup.py;beginline=1;endline=15;md5=718b8f9952f79dfe2d10ad2e7e01f255"

RDEPENDS_${PN} = "python-argparse python-json python-ops-ovsdb python-distribute"

BRANCH ?= "${OPS_REPO_BRANCH}"

SRC_URI = "${OPS_REPO_BASE_URL}/ops-cfgd;protocol=${OPS_REPO_PROTOCOL};branch=${BRANCH} \
           file://cfgd.service \
"

SRCREV = "817cd94adfff9160243737ac6467baed26604dcf"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

do_install_prepend() {
     install -d ${D}${systemd_unitdir}/system
     install -m 0644 ${WORKDIR}/cfgd.service ${D}${systemd_unitdir}/system/
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "cfgd.service"

inherit openswitch setuptools systemd
