SUMMARY = "OpenSwitch Node Exporter"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI = "git://github.com/prometheus/node_exporter.git;branch=master"
PV="0.12.0rc3"
SRCREV = "${PV}"
S = "${WORKDIR}/git"

SRC_URI += " \
    file://node-exporter.service \
    file://extra-ldflags.patch"

inherit go

do_compile() {
    mkdir -p ${S}/src/github.com/prometheus
    ln -sf ${S} ${S}/src/github.com/prometheus/node_exporter
    EXTRA_LDFLAGS="${CGO_LDFLAGS}" make
}

do_install_prepend() {
    install -d ${D}${bindir}
    install -m0755 ${S}/node_exporter ${D}${bindir}/
    install -d ${D}${systemd_unitdir}/system
    install -m 644 ${WORKDIR}/node-exporter.service \
         ${D}${systemd_unitdir}/system/
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "node-exporter.service"

inherit systemd
