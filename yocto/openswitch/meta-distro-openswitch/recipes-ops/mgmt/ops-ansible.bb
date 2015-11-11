SUMMARY = "OpenSwitch ansible modules/playbooks"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS = ""

RDEPENDS_${PN} = "python-syslog python-unixadmin python-ast"

SRC_URI = "git://git.openswitch.net/openswitch/ops-ansible;protocol=http"

SRCREV = "${AUTOREV}"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

ANSIBLE_D := "/usr/share/ansible"

do_install_append() {
    # It's now just a placeholder.
    install -d ${D}${ANSIBLE_D}
}

# This line is required to avoid the warning message.
FILES_${PN} += "${ANSIBLE_D}"

inherit openswitch
