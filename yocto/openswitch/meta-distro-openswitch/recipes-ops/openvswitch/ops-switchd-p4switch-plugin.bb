SUMMARY = "OpenSwitch OVS P4 switch plugin"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

PROVIDES += "virtual/ops-switchd-switch-api-plugin"
RPROVIDES_${PN} += "virtual/ops-switchd-switch-api-plugin"

PV = "git${SRCPV}"

BRANCH ?= "${OPS_REPO_BRANCH}"

SRC_URI = "gitsm://${OPS_REPO_HOSTNAME}/${OPS_REPO_PATH}/ops-switchd-p4switch-plugin;protocol=${OPS_REPO_PROTOCOL};branch=${BRANCH}"

SRCREV = "064c26b304877df3fe40ff3c766646fd488acd18"

S = "${WORKDIR}/git"

DEPENDS = "\
        judy \
        libedit \
        nanomsg \
        p4-hlir \
        thrift \
        thrift-native \
        ops-p4c-native \
        ops-p4c-libpd \
        ops-openvswitch \
        ops-classifierd \
"

RDEPENDS_${PN} = " \
     ops-p4dp \
"

inherit openswitch autotools-brokensep pythonnative pkgconfig

FILES_${PN} += "${libdir}/openvswitch/plugins"
FILES_${PN} += "/usr/share/ovs_p4_plugin/switch_bmv2.json"

EXTRA_OECONF = "--enable-bmv2 --disable-static CPPFLAGS='${CPPFLAGS} -DHOST_BYTE_ORDER_CALLER'"
