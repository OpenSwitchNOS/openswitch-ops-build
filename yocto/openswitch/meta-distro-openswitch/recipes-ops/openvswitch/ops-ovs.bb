# (c) Copyright 2015 Hewlett Packard Enterprise Development LP
#
#    Licensed under the Apache License, Version 2.0 (the "License"); you may
#    not use this file except in compliance with the License. You may obtain
#    a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
#    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
#    License for the specific language governing permissions and limitations

enable_refactor="no"

SUMMARY = "OpenVSwitch for OpenSwitch"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS = "openssl python perl systemd ops-utils libtool ops"

SRC_URI = "git://git.openswitch.net/openswitch/ops-ovs;protocol=https;name=ops-ovs \
           git://github.com/openvswitch/ovs.git;protocol=https;name=ovs;destsuffix=git/ovs-sandbox \
           file://configure.ac \
           file://lib_automake.mk \
           file://ovsdb_automake.mk \
           file://libovscommon.sym.in \
           file://libovscommon.pc.in"

SRCREV_ovs = "ab58aa48a198c3210021070f82042d4dea1e4b41"
SRCREV_ops-ovs = "31e527f7a035e39ae90922ee128f0c88527024dc"
SRCREV_FORMAT = "ovs"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

CFLAGS += "-std=gnu99"

#PACKAGES =+ "ops-ovsdb ops-openvswitch python-ops-ovsdb"
#PROVIDES = "${PACKAGES}"

RDEPENDS_${PN} = "openssl procps util-linux-uuidgen util-linux-libuuid \
                  coreutils python perl perl-module-strict sed gawk grep \
                  ops-ovsdb"

RDEPENDS_ops-ovsdb = "ops"

RDEPENDS_python-ops-ovsdb = "python-io python-netclient python-datetime \
                             python-logging python-threading python-math \
                             python-fcntl python-resource"

EXTRA_OECONF += "TARGET_PYTHON=${bindir}/python \
                 TARGET_PERL=${bindir}/perl \
                 --disable-static --enable-shared"
CONFIGUREOPTS = " --build=${BUILD_SYS} \
		  --host=${HOST_SYS} \
		  --target=${TARGET_SYS} \
		  --prefix=${prefix} \
		  --exec_prefix=${exec_prefix} \
		  --bindir=${bindir} \
		  --sbindir=${sbindir} \
		  --libexecdir=${libexecdir} \
		  --datadir=${datadir} \
		  --sysconfdir=${sysconfdir} \
		  --sharedstatedir=${sharedstatedir} \
		  --localstatedir=${localstatedir} \
		  --libdir=${libdir} \
		  --includedir=${includedir} \
		  --oldincludedir=${oldincludedir} \
		  --infodir=${infodir} \
		  --mandir=${mandir} \
		  --disable-silent-rules \
                  --disable-dependency-tracking"

FILES_ops-ovsdb = "/run /var/run /var/log /var/volatile ${bindir}/ovsdb* \
                   ${sbindir}/ovsdb-server ${datadir}/ovsdbmonitor \
                   ${sysconfdir}/openvswitch/  ${libdir}/libovscommon.so.1* \
                   ${libdir}/libovsdb.so.1*  \
                   ${sysconfdir}/tmpfiles.d/openswitch.conf"

inherit python-dir useradd

FILES_python-ops-ovsdb = "${PYTHON_SITEPACKAGES_DIR}/ovs"

FILES_${PN} = "/var/local/openvswitch \
               ${libdir}/libofproto.so.1* \
               ${libdir}/libopenvswitch.so.1* \
               ${libdir}/libsflow.so.1*"

USERADD_PACKAGES = "${PN}"

GROUPADD_PARAM_${PN} ="-g 1020 ovsdb_users"

do_configure_prepend() {
    if [ ${enable_refactor} != "no" ]; then
        touch yes
        install -m 0644 ${WORKDIR}/configure.ac ${S}/ovs-sandbox/configure.ac
        install -m 0644 ${WORKDIR}/lib_automake.mk ${S}/ovs-sandbox/lib/automake.mk
        install -m 0644 ${WORKDIR}/ovsdb_automake.mk ${S}/ovs-sandbox/ovsdb/automake.mk
        install -m 0644 ${WORKDIR}/libovscommon.sym.in ${S}/ovs-sandbox/lib/libovscommon.sym.in
        install -m 0644 ${WORKDIR}/libovscommon.pc.in ${S}/ovs-sandbox/lib/libovscommon.pc.in
        (cd ${S}/ovs-sandbox && ${S}/ovs-sandbox/boot.sh)
    fi
}

do_configure() {
    if [ "${enable_refactor}" != "no" ]; then
        cfgscript="${S}/ovs-sandbox/configure"
        if [ -x "$cfgscript" ] ; then
            set +e
	    (cd ${S}/ovs-sandbox && $cfgscript ${CONFIGUREOPTS} ${EXTRA_OECONF} "$@")
        else
	    bbfatal "no configure script found at $cfgscript"
        fi
    fi
}

do_compile_prepend() {
    if [ "${enable_refactor}" != "no" ]; then
        cp ${STAGING_DIR_TARGET}/usr/share/openvswitch/vswitch.extschema ${S}/ovs-sandbox/vswitchd/vswitch.extschema
        cp ${STAGING_DIR_TARGET}/usr/share/openvswitch/vswitch.ovsschema ${S}/ovs-sandbox/vswitchd/vswitch.ovsschema
        cp ${STAGING_DIR_TARGET}/usr/share/openvswitch/vswitch.xml ${S}/ovs-sandbox/vswitchd/vswitch.xml
        cp ${STAGING_DIR_TARGET}/usr/share/openvswitch/dhcp_leases.extschema ${S}/ovs-sandbox/vswitchd/dhcp_leases.extschema
        cp ${STAGING_DIR_TARGET}/usr/share/openvswitch/dhcp_leases.ovsschema ${S}/ovs-sandbox/vswitchd/dhcp_leases.ovsschema
        cp ${STAGING_DIR_TARGET}/usr/share/openvswitch/dhcp_leases.xml ${S}/ovs-sandbox/vswitchd/dhcp_leases.xml
        cp ${STAGING_DIR_TARGET}/usr/share/openvswitch/configdb.ovsschema ${S}/ovs-sandbox/vswitchd/configdb.ovsschema

        touch ${S}/ovs-sandbox/vswitchd/vswitch.xml
    fi
}

do_compile() {
    if [ "${enable_refactor}" != "no" ]; then
        oe_runmake -w -C ovs-sandbox ofproto/ipfix-entities.def \
	        include/odp-netlink.h \
		lib/vswitch-idl.c lib/vswitch-idl.h \
		lib/vswitch-idl.ovsidl tests/idltest.c tests/idltest.h \
		tests/idltest.ovsidl vtep/vtep-idl.c vtep/vtep-idl.h \
		vtep/vtep-idl.ovsidl ovn/lib/ovn-sb-idl.c ovn/lib/ovn-sb-idl.h \
		ovn/lib/ovn-sb-idl.ovsidl ovn/lib/ovn-nb-idl.c \
		ovn/lib/ovn-nb-idl.h ovn/lib/ovn-nb-idl.ovsidl \
		ovsdb/ovsdb-server
    fi
}

do_install() {
    if [ "${enable_refactor}" != "no" ]; then
        oe_runmake -C ovs-sandbox 'DESTDIR=${D}' \
		install-libLTLIBRARIES \
		install-completionSCRIPTS install-data-local \
		install-dist_pkgdataDATA install-dist_pkgdataSCRIPTS \
		install-dist_scriptsDATA install-dist_scriptsSCRIPTS \
		install-nobase_pkgdataDATA \
		install-pkgconfigDATA \
		install-pkgdataDATA install-scriptsDATA install-scriptsSCRIPTS
    fi
}

do_install_append() {
    if [ "${enable_refactor}" != "no" ]; then
        # Need to remove files to prevent double-install by autotools - these are already installed from ops.
        rm -f ${D}/${prefix}/share/openvswitch/*.ovsschema

        # Install libraries
        install -d ${D}/${libdir}/pkgconfig/
        install -m 0644 ovs-sandbox/lib/libsflow.pc ${D}/${libdir}/pkgconfig/
        install -m 0644 ovs-sandbox/lib/libopenvswitch.pc ${D}/${libdir}/pkgconfig/
        install -m 0644 ovs-sandbox/ofproto/libofproto.pc ${D}/${libdir}/pkgconfig/

        # Install headers
        install -d ${D}/${includedir}/ovs/
        install -m 0644 ovs-sandbox/config.h ${D}/${includedir}/ovs/
        install -d ${D}/${includedir}/ovs/lib
        install -m 0644 ovs-sandbox/lib/*.h ${D}/${includedir}/ovs/lib
        install -d ${D}/${includedir}/ovs/ofproto
        install -m 0644 ovs-sandbox/ofproto/*.h ${D}/${includedir}/ovs/ofproto
        install -d ${D}/${includedir}/ovs/include
        install -m 0644 ovs-sandbox/include/odp-netlink.h ${D}/${includedir}/ovs/include
        install -m 0644 ovs-sandbox/include/openvswitch/*.h ${D}/${includedir}/ovs/include/.

        install -m 0644 lib/*.h ${D}/${includedir}/ovs/lib
        install -m 0644 ofproto/*.h ${D}/${includedir}/ovs/ofproto

        # Install python packages
        install -d ${D}${PYTHON_SITEPACKAGES_DIR}
        mv ${D}/${prefix}/share/openvswitch/python/ovs ${D}${PYTHON_SITEPACKAGES_DIR}

        # Install service
        install -d ${D}${systemd_unitdir}/system
        install -d ${D}/var/local/openvswitch
        install -m 0644 ${WORKDIR}/ovsdb-server.service ${D}${systemd_unitdir}/system/
        install -d ${D}${sysconfdir}/tmpfiles.d
        echo "d /run/openvswitch/ 0770 - ovsdb_users -" > ${D}${sysconfdir}/tmpfiles.d/openswitch.conf
    fi
}

pkg_postinst_ops-ovsdb () {
    if [ "${enable_refactor}" != "no" ]; then
        # Trigger creation of the /run files
        if [ -z "$D" ]; then
            systemd-tmpfiles --create
        fi
    fi
}

do_package() {
}
do_package_write_rpm() {
}

INSANE_SKIP_${PN} = "installed-vs-shipped"

SYSTEMD_PACKAGES = "ops-ovsdb"

SYSTEMD_SERVICE_ops-ovsdb = "ovsdb-server.service"

inherit openswitch pkgconfig systemd
