HOMEPAGE = "https://github.com/docker/containerd"
SUMMARY = "containerd is a daemon to control runC"
DESCRIPTION = "containerd is a daemon to control runC, built for performance and density. \
               containerd leverages runC's advanced features such as seccomp and user namespace \
               support as well as checkpoint and restore for cloning and live migration of containers."

SRCREV = "ca47f7e76a93e9b3768ed084d62318e85bd9f4b2"
SRC_URI = "\
	git://github.com/docker/containerd.git;nobranch=1 \
	"

# Apache-2.0 for containerd
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.code;md5=aadc30f9c14d876ded7bedc0afd2d3d7"

S = "${WORKDIR}/git"

CONTAINERD_VERSION = "0.2.0"
PV = "${CONTAINERD_VERSION}+git${SRCREV}"

CONTAINERD_PKG="github.com/docker/containerd"

inherit go

do_configure[noexec] = "1"

do_compile() {
        mkdir -p ${S}/src/`dirname ${CONTAINERD_PKG}`
        ln -sf ${S} ${S}/src/${CONTAINERD_PKG}
        export GOPATH="${S}:${S}/vendor"
        oe_runmake static
}

# Note: disabled for now, since docker is launching containerd
# inherit systemd
# SYSTEMD_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES','systemd','${PN}','',d)}"
# SYSTEMD_SERVICE_${PN} = "${@bb.utils.contains('DISTRO_FEATURES','systemd','containerd.service','',d)}"

do_install() {
	mkdir -p ${D}/${bindir}

	cp ${S}/bin/containerd ${D}/${bindir}/containerd
	cp ${S}/bin/containerd-shim ${D}/${bindir}/containerd-shim
        cp ${S}/bin/ctr ${D}/${bindir}/containerd-ctr

	ln -sf containerd ${D}/${bindir}/docker-containerd
	ln -sf containerd-shim ${D}/${bindir}/docker-containerd-shim
	ln -sf containerd-ctr ${D}/${bindir}/docker-containerd-ctr

	install -d ${D}${systemd_unitdir}/system
	install -m 644 ${S}/hack/containerd.service ${D}/${systemd_unitdir}/system
	# adjust from /usr/local/bin to /usr/bin/
	sed -e "s:/usr/local/bin/containerd:${bindir}/docker-containerd -l \"unix\:///var/run/docker/libcontainerd/docker-containerd.sock\":g" -i ${D}/${systemd_unitdir}/system/containerd.service
}

FILES_${PN} += "/lib/systemd/system/*"
