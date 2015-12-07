SUMMARY = "Programs used to build P4 for the P4 Behavioral Model"
HOMEPAGE = "https://github.com/p4lang/p4factory"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2f3453ba8e98aaed11a290758a999e65"

SRC_URI = "\
	git://git.openswitch.net/openswitch/ops-p4factory;protocol=https;branch=feature/p4 \
	file://bmv2.service \
	file://bmv2_unet.service \
	file://ops-init-bmv2-p4.service \
"
SRCREV = "${AUTOREV}"
PV = "git${SRCPV}"
S = "${WORKDIR}/git"

DEPENDS = "\
	judy \
	libedit \
	nanomsg \
	p4-hlir \
	python-native \
	python-pyyaml-native \
	python-tenjin \
	thrift \
	thrift-native \
	ops-openvswitch \
	gmp \
"

RDEPENDS_${PN} = "\
	judy \
	libedit \
	libpcap \
	nanomsg \
	thrift \
"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "bmv2.service"
SYSTEMD_SERVICE_${PN} += "bmv2_unet.service"
SYSTEMD_SERVICE_${PN} += "ops-init-bmv2-p4.service"

inherit pythonnative
inherit autotools-brokensep
inherit openswitch
inherit systemd

LIBTOOL = "${B}/${HOST_SYS}-libtool"
EXTRA_OEMAKE = "'LIBTOOL=${LIBTOOL}'"

FILES_${PN} += "\
	${libdir}/openvswitch/plugins/libovs_p4_plugin.so \
"

FILES_${PN}-dbg += "\
	${libdir}/openvswitch/plugins/.debug/libovs_p4_plugin.so \
"

do_install() {
	# XXX: Move all of this to the makefile

	install -m 0644 -o root -g root -d ${D}/${prefix}/sbin
	install -m 0755 -o root -g root \
		${S}/src/unet/bmv2_unet \
		${D}/${prefix}/sbin/bmv2_unet
	install -m 0755 -o root -g root \
		${S}/submodules/bm/targets/simple_switch/simple_switch \
		${D}/${prefix}/sbin/bmv2
	install -m 0755 -o root -g root \
		${S}/src/ops/ops-init-bmv2-p4 \
		${D}/${prefix}/sbin/ops-init-bmv2-p4

	install -m 0644 -o root -g root -d ${D}/${libdir}/openvswitch/plugins
	install -m 0644 -o root -g root \
		${S}/src/ops-switchd-p4-plugin/libovs_p4_plugin.so \
		${D}/${libdir}/openvswitch/plugins/libovs_p4_plugin.so

	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/bmv2.service ${D}${systemd_unitdir}/system/
	install -m 0644 ${WORKDIR}/bmv2_unet.service ${D}${systemd_unitdir}/system/
	install -m 0644 ${WORKDIR}/ops-init-bmv2-p4.service ${D}${systemd_unitdir}/system/
}
