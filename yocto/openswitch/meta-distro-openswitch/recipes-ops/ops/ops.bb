SUMMARY = "OpenSwitch"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "git://git.openswitch.net/openswitch/ops;protocol=https;branch=feature/ops_config"

SRCREV = "945abdbf4edbc55ecaac4f447e9aaa538667cce1"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

FILES_${PN} = "/usr/share/openvswitch/ /usr/share/openvswitch/*.extschema /usr/share/openvswitch/*.xml /usr/share/openvswitch/*.ovsschema"

OPS_SCHEMA_PATH="${S}/schema"

do_compile() {
  echo ${IMAGE_FEATURES} > ${TOPDIR}/image_features
  ${PYTHON} ${OPS_SCHEMA_PATH}/schemaprune_xml.py -i ${OPS_SCHEMA_PATH}/vswitch.xml -o ${OPS_SCHEMA_PATH}/vswitch.untag.xml -f ${TOPDIR}/image_features -l ${TOPDIR}/vswitch.xml.schemaprune.log -L INFO -S False
  ${PYTHON} ${OPS_SCHEMA_PATH}/schemaprune_xml.py -i ${OPS_SCHEMA_PATH}/dhcp_leases.xml -o ${OPS_SCHEMA_PATH}/dhcp_leases.untag.xml -f ${TOPDIR}/image_features -l ${TOPDIR}/dhcp_leases.xml.schemaprune.log -L INFO -S False
  ${PYTHON} ${OPS_SCHEMA_PATH}/schemaprune_json.py -i ${OPS_SCHEMA_PATH}/vswitch.extschema -o ${OPS_SCHEMA_PATH}/vswitch.untag.extschema -f ${TOPDIR}/image_features -l ${TOPDIR}/vswitch.extschema.schemaprune.log -L INFO -S False
  ${PYTHON} ${OPS_SCHEMA_PATH}/schemaprune_json.py -i ${OPS_SCHEMA_PATH}/dhcp_leases.extschema -o ${OPS_SCHEMA_PATH}/dhcp_leases.untag.extschema -f ${TOPDIR}/image_features -l ${TOPDIR}/dhcp_leases.extschema.schemaprune.log -L INFO -S False
  ${PYTHON} ${OPS_SCHEMA_PATH}/sanitize.py ${OPS_SCHEMA_PATH}/vswitch.untag.extschema ${OPS_SCHEMA_PATH}/vswitch.ovsschema
  ${PYTHON} ${OPS_SCHEMA_PATH}/sanitize.py ${OPS_SCHEMA_PATH}/dhcp_leases.untag.extschema ${OPS_SCHEMA_PATH}/dhcp_leases.ovsschema
}

do_install() {
  install -d ${D}/${prefix}/share/openvswitch
	install -m 0644 ${OPS_SCHEMA_PATH}/vswitch.untag.extschema ${D}/${prefix}/share/openvswitch/vswitch.extschema
	install -m 0644 ${OPS_SCHEMA_PATH}/vswitch.ovsschema ${D}/${prefix}/share/openvswitch/vswitch.ovsschema
	install -m 0644 ${OPS_SCHEMA_PATH}/vswitch.untag.xml ${D}/${prefix}/share/openvswitch/vswitch.xml
	install -m 0644 ${OPS_SCHEMA_PATH}/dhcp_leases.untag.extschema ${D}/${prefix}/share/openvswitch/dhcp_leases.extschema
	install -m 0644 ${OPS_SCHEMA_PATH}/dhcp_leases.ovsschema ${D}/${prefix}/share/openvswitch/dhcp_leases.ovsschema
	install -m 0644 ${OPS_SCHEMA_PATH}/dhcp_leases.untag.xml ${D}/${prefix}/share/openvswitch/dhcp_leases.xml
	install -m 0644 ${OPS_SCHEMA_PATH}/configdb.ovsschema ${D}/${prefix}/share/openvswitch/configdb.ovsschema
}
