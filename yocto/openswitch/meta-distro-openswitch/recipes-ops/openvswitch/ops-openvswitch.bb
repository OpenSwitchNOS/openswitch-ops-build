SUMMARY = "OpenVSwitch for OpenSwitch"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS = "openssl python perl systemd libtool libyaml jemalloc ops"

SRC_URI = "git://github.com/openvswitch/ovs.git;protocol=https;branch=branch-2.5 \
           file://ovsdb-server.service \
           file://0001-Sync-OVS-2.5-to-OPS.patch \
           file://0002-Fix-compile-error-in-opennsl-plugin.patch \
           file://0003-Compatibility-with-C-11.patch \
           file://0004-Fix-IDL-generation-to-emit-enums-with-a-single-optio.patch \
           file://0005-Add-support-for-column-tracking-in-IDL.patch \
           file://0006-Ignore-build-artifacts.patch \
           file://0007-Make-schema-cksum-validation-consistent.patch \
           file://0008-Add-json_object_get_string-API.patch \
           file://0009-Rename-ovsdb_users-to-ovsdb-client.patch \
           file://0010-Vtep-IDL-file-generation.patch \
           file://0011-vswitchd-changes-for-L3-statistics.patch \
           file://0012-Add-Resilient-ECMP-CLI.patch \
           file://0013-OSPFv2-key-and-default-definitions.patch \
           file://0014-sFlow-related-changes.patch \
           file://0015-Converted-tests.patch \
           file://0016-Partial-map-updates.patch \
           file://0017-On-demand-fetching.patch \
           file://0018-Compound-indexes.patch \
           file://0019-Python-IDL-tracking.patch \
           file://0020-smap-shash-add-numeric-and-flexible-sort.patch \
           file://0021-Handle-special-characters-in-Python-JSON-parser.patch \
           file://0022-Populate-value-for-key-value-pair-of-max1.patch \
           file://0023-Fix-for-weak-key-strong-value.patch \
           file://0024-Improve-ovsdb-json-error-msgs.patch \
           file://0025-Implementation-of-weak_gc-reference-type.patch \
           "

SRCREV = "976b441345df85dd7adb973f8ae208f588d764a0"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

inherit openswitch autotools distutils pkgconfig systemd useradd

EXTRA_OECONF += "TARGET_PYTHON=${bindir}/python \
                 TARGET_PERL=${bindir}/perl \
                 --disable-static --enable-shared LIBS=-ljemalloc \
                 ${@bb.utils.contains('MACHINE_FEATURES', 'ops-container', '--enable-simulator-provider', '',d)} \
                 "
do_configure_prepend() {
    export OPEN_HALON_BUILD=1
    export OPS_BUILD=1
    export BUILD_OVS_VSWITCHD=0
    export BUILD_PLUGINS_LIB=1
    # After building the code with libltdl, we get a subdirectory with autoconf that will
    # inherit the m4 macros configurations from his parent, causing to fail if not finding some
    # of their macros. This hack removes the issue
    if [ -d ${S}/libltdl ] ; then
        if ! [ -L ${S}/libltdl/m4 ] ; then
            ln -s ../m4 ${S}/libltdl/m4
        fi
    fi
}

do_compile() {
    cp ${STAGING_DIR_TARGET}/usr/share/openvswitch/vswitch.extschema ${S}/vswitchd/vswitch.extschema
    cp ${STAGING_DIR_TARGET}/usr/share/openvswitch/vswitch.ovsschema ${S}/vswitchd/vswitch.ovsschema
    cp ${STAGING_DIR_TARGET}/usr/share/openvswitch/vswitch.xml ${S}/vswitchd/vswitch.xml
    cp ${STAGING_DIR_TARGET}/usr/share/openvswitch/dhcp_leases.extschema ${S}/vswitchd/dhcp_leases.extschema
    cp ${STAGING_DIR_TARGET}/usr/share/openvswitch/dhcp_leases.ovsschema ${S}/vswitchd/dhcp_leases.ovsschema
    cp ${STAGING_DIR_TARGET}/usr/share/openvswitch/dhcp_leases.xml ${S}/vswitchd/dhcp_leases.xml
    cp ${STAGING_DIR_TARGET}/usr/share/openvswitch/configdb.ovsschema ${S}/vswitchd/configdb.ovsschema
    cp ${STAGING_DIR_TARGET}/usr/share/openvswitch/vtep.ovsschema ${S}/vswitchd/vtep.ovsschema
    cp ${STAGING_DIR_TARGET}/usr/share/openvswitch/vtep.xml ${S}/vswitchd/vtep.xml

    touch ${S}/vswitchd/vswitch.xml

    base_do_compile

    cd ${S}/python
    distutils_do_compile
}

do_install() {
    cd ${B}
    autotools_do_install

    cd ${S}/python
    distutils_do_install

    cd ${S}
    # Need to remove files to prevent double-install by autotools - these are already installed from ops.
    rm -f ${D}/${prefix}/share/openvswitch/*.ovsschema

    install -m 0644 lib/libovscommon.pc ${D}/${libdir}/pkgconfig/
    install -m 0644 lib/libsflow.pc ${D}/${libdir}/pkgconfig/
    install -m 0644 lib/libopenvswitch.pc ${D}/${libdir}/pkgconfig/
    install -m 0644 ofproto/libofproto.pc ${D}/${libdir}/pkgconfig/
    install -m 0644 ovsdb/libovsdb.pc ${D}/${libdir}/pkgconfig/

    install -d ${D}${systemd_unitdir}/system
    install -d ${D}/var/local/openvswitch
    install -m 0644 ${WORKDIR}/ovsdb-server.service ${D}${systemd_unitdir}/system/
    install -d ${D}${sysconfdir}/tmpfiles.d
    echo "d /run/openvswitch/ 0770 - ovsdb-client -" > ${D}${sysconfdir}/tmpfiles.d/openswitch.conf
    echo "a+ /run/log/journal/%m - - - - d:group:ops_netop:r-x" >> ${D}${sysconfdir}/tmpfiles.d/openswitch.conf
    echo "A+ /run/log/journal/%m - - - - group:ops_netop:r-x" >> ${D}${sysconfdir}/tmpfiles.d/openswitch.conf

    # Remove python dir in favor of the one installed by distutils
    rm -Rf ${D}/${prefix}/share/openvswitch/python/ovs
}

pkg_postinst_ops-ovsdb () {
        # Trigger creation of the /run files
	if [ -z "$D" ]; then
		systemd-tmpfiles --create
	fi
}

INSANE_SKIP_${PN} = "installed-vs-shipped"

SYSTEMD_PACKAGES = "${PN} ops-ovsdb"

SYSTEMD_SERVICE_ops-ovsdb = "ovsdb-server.service"

PACKAGES = "${PN}-dbg ${PN}-staticdev ${PN}-dev ${PN}-docs ${PN} ops-ovsdb python-ops-ovsdb"
PROVIDES = "${PACKAGES}"

RDEPENDS_${PN} = "openssl procps util-linux-uuidgen util-linux-libuuid coreutils \
  python perl perl-module-strict sed gawk grep ops-ovsdb bash \
  ${@bb.utils.contains('MACHINE_FEATURES', 'ops-container', 'openvswitch-sim-switch', '',d)} \
"

RDEPENDS_${PN}_remove := "${@bb.utils.contains("IMAGE_FEATURES", "ops-p4", "openvswitch-sim-switch", "",d)}"

RDEPENDS_ops-ovsdb = "ops"

RDEPENDS_python-ops-ovsdb = "python-io python-netclient python-datetime \
  python-logging python-threading python-math python-fcntl python-resource"

FILES_ops-ovsdb = "/run /var/run /var/log /var/volatile ${bindir}/ovsdb* \
  ${sbindir}/ovsdb-server ${datadir}/ovsdbmonitor ${sysconfdir}/openvswitch/ \
  ${libdir}/libovscommon.so.1* ${libdir}/libovsdb.so.1* \
  ${sysconfdir}/tmpfiles.d/openswitch.conf"

FILES_python-ops-ovsdb = "${libdir}/${PYTHON_DIR}/*"

FILES_${PN} = "${bindir}/ovs-appctl ${bindir}/ovs-pki ${bindir}/ovs-vsctl \
 /var/local/openvswitch /etc/bash_completion.d \
 ${libdir}/libofproto.so.1* \
 ${libdir}/libopenvswitch.so.1* \
 ${libdir}/libsflow.so.1* \
 ${libdir}/libplugins.so.1* \
 ${libdir}/libvtep.so.1* \
"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} ="-g 1020 ovsdb-client;ops_netop;ops_admin"
