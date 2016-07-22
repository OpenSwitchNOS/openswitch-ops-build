HOMEPAGE = "http://www.docker.com"
SUMMARY = "Linux container runtime"
DESCRIPTION = "Linux container runtime \
 Docker complements kernel namespacing with a high-level API which \
 operates at the process level. It runs unix processes with strong \
 guarantees of isolation and repeatability across servers. \
 . \
 Docker is a great building block for automating distributed systems: \
 large-scale web deployments, database clusters, continuous deployment \
 systems, private PaaS, service-oriented architectures, etc. \
 . \
 This package contains the daemon and client. Using docker.io on non-amd64 \
 hosts is not supported at this time. Please be careful when using it \
 on anything besides amd64. \
 . \
 Also, note that kernel version 3.8 or above is required for proper \
 operation of the daemon process, and that any lower versions may have \
 subtle and/or glaring issues. \
 "

SRCREV = "5604cbed50d51c4039b1abcb1cf87c4e01bce924"
SRC_URI = "\
	git://github.com/docker/docker.git;nobranch=1 \
	file://docker.service \
	"

# Apache-2.0 for docker
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cc2221abf0b96ea39dd68141b70f7937"

S = "${WORKDIR}/git"

DOCKER_VERSION = "1.11.1"
PV = "${DOCKER_VERSION}+git${SRCREV}"

DEPENDS = "\
    btrfs-tools \
    sqlite3 \
    systemd \
    "

#DEPENDS_append_class-target = "lvm2"
RDEPENDS_${PN} = "curl aufs-util git cgroup-lite util-linux iptables"
RDEPENDS_${PN} += "containerd runc"
RRECOMMENDS_${PN} += " kernel-module-dm-thin-pool kernel-module-nf-nat"
DOCKER_PKG="github.com/docker/docker"

do_configure[noexec] = "1"

do_compile() {
        mkdir -p ${S}/src/`dirname ${DOCKER_PKG}`
        ln -sf ${S} ${S}/src/${DOCKER_PKG}
        export GOPATH="${S}:${S}/vendor"

	# in order to exclude devicemapper and btrfs - https://github.com/docker/docker/issues/14056
	export DOCKER_BUILDTAGS='exclude_graphdriver_btrfs exclude_graphdriver_devicemapper'

	# this is the unsupported built structure
	# that doesn't rely on an existing docker
	# to build this:
	DOCKER_GITCOMMIT="${SRCREV}" \
	  ./hack/make.sh dynbinary
}

inherit go systemd

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "docker.service"

do_install() {
	mkdir -p ${D}/${bindir}
	cp ${S}/bundles/${DOCKER_VERSION}/dynbinary/docker-${DOCKER_VERSION} ${D}/${bindir}/docker

	install -d ${D}${systemd_unitdir}/system
	install -m 644 ${S}/contrib/init/systemd/docker.* ${D}/${systemd_unitdir}/system
	# replaces one copied from above with one that uses the local registry for a mirror
	install -m 644 ${WORKDIR}/docker.service ${D}/${systemd_unitdir}/system
}

inherit useradd
USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "-r docker"

FILES_${PN} += "/lib/systemd/system/*"

# DO NOT STRIP docker
INHIBIT_PACKAGE_STRIP = "1"
