SUMMARY = "An open source implementation of the BFD protocol"
LICENSE = "BSD"
HOMEPAGE = "https://github.com/dyninc/OpenBFDD"

LIC_FILES_CHKSUM = "file://LICENSE;md5=69966e3881d50a528d1e93dd31615502"

DEPENDS = "ops-utils ops-ovsdb ops-cli"

SRC_URI = "git://github.com/dyninc/OpenBFDD.git;protocol=https \
	file://bfdd-beacon.service \
"

SRCREV = "895cfb523bb96b3ef199fc5916578482ccd528ee"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

RDEPENDS_${PN} = "openssl procps util-linux-uuidgen util-linux-libuuid coreutils \
  python perl perl-module-strict sed gawk grep \
  ops-openvswitch ops-ovsdb \
  ${@bb.utils.contains('MACHINE_FEATURES', 'ops-container', 'openvswitch-sim-switch', '',d)} \
"

FILES_${PN} += "/usr/share/opsplugins /usr/lib/cli/plugins/"
do_install_append() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/bfdd-beacon.service ${D}${systemd_unitdir}/system/
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "bfdd-beacon.service"

inherit openswitch autotools pkgconfig systemd
export OVS_INCLUDE="${STAGING_DIR_TARGET}/usr/include/ovs"
