SUMMARY = "OpenSwitch Supportability Features"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS = "ops-ovsdb libyaml ops-cli"

RDEPENDS_${PN} = "python-argparse python-json python-ops-ovsdb python-distribute"

SRC_URI = "git://git.openswitch.net/openswitch/ops-supportability;protocol=https"

SRCREV = "074590d0d422fa724a41b9f0b2f52d4aad1cc0af"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
#PV = "git${SRCPV}"

S = "${WORKDIR}/git"

# Mixing of two classes, the build happens on the source directory.
inherit openswitch cmake setuptools systemd

# Doing some magic here. We are inheriting cmake and setuptools classes, so we
# need to override the exported functions and call both by ourselves.
do_compile() {
     cd ${S}
     distutils_do_compile
     # Cmake compile changes to the B directory
     cmake_do_compile
}

do_install() {
     cd ${S}
     distutils_do_install
     # Cmake compile changes to the B directory
     cmake_do_install
}

do_install_append(){
   install -d   ${D}/etc/openswitch/supportability
   install -c -m 755 ${S}/conf/*.yaml ${D}/etc/openswitch/supportability/
}

FILES_${PN} += "/usr/lib/cli/plugins/ \
                /etc/openswitch/supportability"

SYSTEMD_PACKAGES = "${PN}"
