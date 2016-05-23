SUMMARY = "OpenVSwitch for OpenSwitch"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS = "openssl python perl systemd libtool libyaml jemalloc ops"

SRC_URI = "git://github.com/openvswitch/ovs.git;protocol=https;branch=branch-2.5 \
           file://ovsdb-server.service \
           file://0001-Sync-OVS-2.5-to-OPS.patch \
           file://0002-Remove-LAG-L2-VID-hash-Load-Balance.patch \
           file://0003-Fix-compile-error-in-opennsl-plugin.patch \
           file://0004-Compatibility-with-C-11.patch \
           file://0005-Fix-IDL-generation-to-emit-enums-with-a-single-optio.patch \
           file://0006-Add-support-for-column-tracking-in-IDL.patch \
           file://0007-Ignore-build-artifacts.patch \
           file://0008-Make-schema-cksum-validation-consistent.patch \
           file://0009-Add-json_object_get_string-API.patch \
           file://0010-Rename-ovsdb_users-to-ovsdb-client.patch \
           file://0011-new-dev-Vtep-IDL-file-generation.patch \
           file://0012-new-dev-vswitchd-changes-for-L3-statistics.patch \
           file://0013-chg-dev-Modularized-NTP-CLIs-to-be-present-at-ops-nt.patch \
           file://0014-new-dev-Added-a-new-IDL-identifier-for-proxy-ARP.patch \
           file://0015-Add-Resilient-ECMP-CLI.patch \
           file://0016-chg-usr-BGP-openvswitch-changes-commands-added-set-a.patch \
           file://0017-Defining-DHCP-Relay-key-in-openswitch-idl-library.patch \
           file://0018-UDP-Bcast-Forwarder-global-configuration-key-macro.patch \
           file://0019-new-dev-Adding-counter-definitions-for-BGP-clear-com.patch \
           file://0020-Intoduce-support-of-QSFP28-connector-type.patch \
           file://0021-new-dev-OSPFv2-key-and-default-definitions.patch \
           file://0022-chg-usr-Configure-LAG-with-fallback.patch \
           file://0023-new-dev-Added-a-new-IDL-identifier-for-Local-Proxy-A.patch \
           file://0024-chg-dev-Defining-DHCP-configuration-keys.patch \
           file://0025-chg-dev-sFlow-related-changes.patch \
           file://0026-chg-dev-Defining-DHCP-Relay-Statistics-keys-in-opens.patch \
           file://0027-new-dev-Add-new-key-for-LACP-Fallback.patch \
           file://0028-new-dev-Adding-CoPP-global-stats-related-defines.patch \
           file://0029-Added-broadview-config-related-macros.patch \
           file://0030-new-dev-Add-definitions-for-bond-status.patch \
           file://0031-new-dev-Macro-to-support-global-DNS-client-feature.patch \
           file://0032-new-dev-Add-definitions-for-LACP-Fallback-mode.patch \
           file://0033-Added-macros-enums-for-port-state-pecking-order.patch \
           file://0034-Converted-tests.patch \
           file://0035-Partial-map-updates.patch \
           file://0036-On-demand-fetching.patch \
           file://0037-Compound-indexes.patch \
           file://0038-Python-IDL-tracking.patch \
           file://0039-smap-shash-add-numeric-and-flexible-sort.patch \
           file://0040-Handle-special-characters-in-Python-JSON-parser.patch \
           "

SRCREV = "976b441345df85dd7adb973f8ae208f588d764a0"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

PACKAGES =+ "ops-ovsdb python-ops-ovsdb"
PROVIDES = "${PACKAGES}"

RDEPENDS_${PN} = "openssl procps util-linux-uuidgen util-linux-libuuid coreutils \
  python perl perl-module-strict sed gawk grep ops-ovsdb \
  ${@bb.utils.contains('MACHINE_FEATURES', 'ops-container', 'openvswitch-sim-switch', '',d)} \
"

RDEPENDS_${PN}_remove := "${@bb.utils.contains("IMAGE_FEATURES", "ops-p4", "openvswitch-sim-switch", "",d)}"

RDEPENDS_ops-ovsdb = "ops"

RDEPENDS_python-ops-ovsdb = "python-io python-netclient python-datetime \
  python-logging python-threading python-math python-fcntl python-resource"

EXTRA_OECONF += "TARGET_PYTHON=${bindir}/python \
                 TARGET_PERL=${bindir}/perl \
                 --disable-static --enable-shared LIBS=-ljemalloc \
                 ${@bb.utils.contains('MACHINE_FEATURES', 'ops-container', '--enable-simulator-provider', '',d)} \
                 "
FILES_ops-ovsdb = "/run /var/run /var/log /var/volatile ${bindir}/ovsdb* \
  ${sbindir}/ovsdb-server ${datadir}/ovsdbmonitor ${sysconfdir}/openvswitch/ \
  ${libdir}/libovscommon.so.1* ${libdir}/libovsdb.so.1* \
  ${sysconfdir}/tmpfiles.d/openswitch.conf"

inherit python-dir useradd

FILES_python-ops-ovsdb = "${PYTHON_SITEPACKAGES_DIR}/ovs"

FILES_${PN} = "${bindir}/ovs-appctl ${bindir}/ovs-pki ${bindir}/ovs-vsctl \
 /var/local/openvswitch \
 ${libdir}/libofproto.so.1* \
 ${libdir}/libopenvswitch.so.1* \
 ${libdir}/libsflow.so.1* \
 ${libdir}/libplugins.so.1* \
 ${libdir}/libvtep.so.1* \
"

USERADD_PACKAGES = "${PN}"

GROUPADD_PARAM_${PN} ="-g 1020 ovsdb-client;ops_netop;ops_admin"

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

do_compile_prepend() {
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
}

do_install_append() {
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
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}
    mv ${D}/${prefix}/share/openvswitch/python/ovs ${D}${PYTHON_SITEPACKAGES_DIR}
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

inherit openswitch autotools pkgconfig systemd
