SUMMARY = "OpenSwitch Prometheus"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI = "git://github.com/prometheus/prometheus.git;branch=release-0.17"
PV="0.17.0rc2"
SRCREV = "${PV}"
S = "${WORKDIR}/git"

SRC_URI += " \
    file://standard.rules \
    file://prometheus.yml \
    file://prometheus.service \
    file://prometheus.nginx \
    file://extra-ldflags.patch \
    file://make-clean.patch"

inherit go systemd useradd

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system --home /var/volatile/prometheus/ \
    --user-group prometheus"

do_compile() {
    export GOPATH=${S}:${STAGING_LIBDIR}/${TARGET_SYS}/go

    mkdir -p ${S}/src/github.com/prometheus
    ln -sf ${S} ${S}/src/github.com/prometheus/prometheus
    EXTRA_LDFLAGS="${CGO_LDFLAGS}" make build
}

do_install() {
    install -d ${D}${bindir}
    install -m0755 ${S}/prometheus ${D}${bindir}/
    install -m0755 ${S}/promtool ${D}${bindir}/
    install -d ${D}${sysconfdir}/prometheus/rules.d
    install -m0640 -o prometheus -g prometheus \
        ${WORKDIR}/prometheus.yml ${D}/${sysconfdir}/prometheus/
    install -m0644 ${WORKDIR}/standard.rules \
        ${D}/${sysconfdir}/prometheus/rules.d/
    install -d ${D}${systemd_unitdir}/system
    install -m 644 ${WORKDIR}/prometheus.service \
        ${D}${systemd_unitdir}/system/
    install -d ${D}/etc/nginx/conf.d
    install -m 0644 ${WORKDIR}/prometheus.nginx \
        ${D}/etc/nginx/conf.d/backend-prometheus.conf
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "prometheus.service"
SYSTEMD_AUTO_ENABLE = "no"
