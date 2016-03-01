SUMMARY = "OpenSwitch WebUI"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "git://git.openswitch.net/openswitch/ops-webui;protocol=http \
"

SRCREV = "3897b2782712e6dbc798785565bb18e8a0fa416a"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"
B = "${S}"

inherit npm

# Override the NPM dependency on node, it is only used compile the resources
RDEPENDS_${PN} = ""

do_compile() {
    ./tools/scripts/extract-node-tars
    oe_runnpm run test
    oe_runnpm run buildprod
    oe_runnpm run testcover
    make -C errors/
}

do_install() {
    install -d ${D}/srv/www/static
    cp -Rp build/* ${D}/srv/www/static
    install -d  ${D}/srv/www/static/error
    install -m0644 errors/build/*.html ${D}/srv/www/static/error/
}

FILES_${PN} = "/srv/www/static/*"
