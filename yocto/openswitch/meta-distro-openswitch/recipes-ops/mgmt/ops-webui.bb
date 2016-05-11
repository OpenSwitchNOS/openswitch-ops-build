SUMMARY = "OpenSwitch WebUI"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

BRANCH ?= "${OPS_REPO_BRANCH}"

SRC_URI = "${OPS_REPO_BASE_URL}/ops-webui;protocol=${OPS_REPO_PROTOCOL};branch=${BRANCH} \
"

SRCREV = "3e603504d3cefb104c49b51a88e41d37a7383f22"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"
B = "${S}"

inherit npm

# Put it after the inherit NPM to override the dependency on node
RDEPENDS_${PN} = "ops-restd"

do_compile() {
    ./tools/scripts/extract-node-tars
    oe_runnpm run testcover
    oe_runnpm run buildprod
}

do_install() {
    install -d ${D}/srv/www/static
    cp -R build/* ${D}/srv/www/static
}

FILES_${PN} = "/srv/www/static/*"
