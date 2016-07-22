HOMEPAGE = "https://github.com/opencontainers/runc"
SUMMARY = "runc container cli tools"
DESCRIPTION = "runc is a CLI tool for spawning and running containers according to the OCI specification."

# Note: this rev is before the required protocol field, update when all components
#       have been updated to match.
SRCREV = "baf6536d6259209c3edfa2b22237af82942d3dfa"
SRC_URI = "\
	git://github.com/opencontainers/runc;branch=master \
	"

# Apache-2.0 for containerd
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=435b266b3899aa8a959f17d41c56def8"

S = "${WORKDIR}/git"

RUNC_VERSION = "0.1.1"
PV = "${RUNC_VERSION}+git${SRCREV}"

LIBCONTAINER_PACKAGE="github.com/opencontainers/runc/libcontainer"

do_configure[noexec] = "1"
EXTRA_OEMAKE="BUILDTAGS=''"

inherit go

do_compile() {
        mkdir -p ${S}/src/`dirname ${LIBCONTAINER_PACKAGE}`
        ln -sf ${S}/libcontainer ${S}/src/${LIBCONTAINER_PACKAGE}
        export GOPATH="${S}:${S}/vendor"
        oe_runmake static
}

do_install() {
	mkdir -p ${D}/${bindir}

	cp ${S}/runc ${D}/${bindir}/runc
	ln -sf runc ${D}/${bindir}/docker-runc
}
