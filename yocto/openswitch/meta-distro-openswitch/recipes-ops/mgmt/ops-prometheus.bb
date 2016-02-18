SUMMARY = "OpenSwitch Prometheus"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI = "git://github.com/prometheus/prometheus.git;branch=release-0.17"
PV="0.17.0rc2"
SRCREV = "${PV}"
#RDEPENDS_${PN} = "lighttpd"
S = "${WORKDIR}/git"

PACKAGES += "${PN}-config"
RDEPENDS_${PN} += "${PN}-config"

SRC_URI += " \
    file://standard.rules \
    file://prometheus.yml \
    file://prometheus.service \
    file://extra-ldflags.patch \
    file://make-clean.patch"

inherit go systemd

FILES_${PN} = "${bindir} ${systemd_unitdir}"
FILES_${PN}-config = "${sysconfdir}"

do_compile() {
    export GOPATH=${S}:${STAGING_LIBDIR}/${TARGET_SYS}/go

    mkdir -p ${S}/src/github.com/prometheus
    ln -sf ${S} ${S}/src/github.com/prometheus/prometheus
    EXTRA_LDFLAGS="${CGO_LDFLAGS}" make build
}

do_install_prepend() {
    install -d ${D}${bindir}
    install -m0755 ${S}/prometheus ${D}${bindir}/
    install -m0755 ${S}/promtool ${D}${bindir}/
    install -d ${D}${sysconfdir}/prometheus/rules.d
    install -m0640 ${WORKDIR}/prometheus.yml \
         ${D}/${sysconfdir}/prometheus/
    install -m0644 ${WORKDIR}/standard.rules \
         ${D}/${sysconfdir}/prometheus/rules.d/
    install -d ${D}${systemd_unitdir}/system
    install -m 644 ${WORKDIR}/prometheus.service \
         ${D}${systemd_unitdir}/system/
}

pkg_postinst_${PN}-config () {
    if [ x"$D" != "x" ]; then
        exit 1
    fi
    set -e
    chown opsd.ovsdb-client ${sysconfdir}/prometheus/prometheus.yml
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "prometheus.service"
