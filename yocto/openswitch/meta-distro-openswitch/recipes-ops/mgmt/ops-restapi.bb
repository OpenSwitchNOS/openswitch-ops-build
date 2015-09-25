SUMMARY = "OpenSwitch REST API rendering"
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

inherit npm

# Put it after the inherit NPM to override the dependency on node
#RDEPENDS_${PN} = "lighttpd"

do_compile() {
    oe_runnpm install     # Installs dependencies defined in package.json
    oe_runnpm run buildprod
}

do_install() {
    install -d ${D}/srv/www/restapi
    cp -Rp build/* ${D}/srv/www/restapi
}

FILES_${PN} = "/srv/www/restapi/*"
