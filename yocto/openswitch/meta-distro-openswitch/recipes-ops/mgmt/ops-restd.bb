SUMMARY = "OpenSwitch REST Service Daemon"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://setup.py;beginline=1;endline=15;md5=718b8f9952f79dfe2d10ad2e7e01f255"

DEPENDS = "python-inflect-native python-tornado-native ops-openvswitch ops-ovsdb ops-supportability"

RDEPENDS_${PN} = "python-argparse python-json python-ops-ovsdb python-distribute python-tornado python-html python-pkgutil python-subprocess python-numbers python-inflect python-xml ops-restapi python-unixadmin python-jsonschema python-jsonpatch ops-aaa-utils ops-passwd-srv python-pycrypto"

SRC_URI = "git://git.openswitch.net/openswitch/ops-restd;protocol=http;branch=rel/dill \
           file://restd.service \
"

SRCREV = "1a869a18a2ff86e7eac216562411d8f4437ac2c3"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

do_install_prepend() {
     install -d ${D}${systemd_unitdir}/system
     install -m 0644 ${WORKDIR}/restd.service ${D}${systemd_unitdir}/system/
}

do_install_append () {
      # Generating REST API file for use by ops-restapi module
      install -d ${D}/srv/www/api
      cd ${S}/opslib
      # We do not have a native ovsdb-python package, so we use the one
      # from the target by hacking the PYTHONPATH
      PYTHONPATH=${STAGING_DIR_TARGET}/${PYTHON_SITEPACKAGES_DIR}:${PYTHONPATH} ${PYTHON} apidocgen.py ${STAGING_DIR_TARGET}/${prefix}/share/openvswitch/vswitch.extschema ${STAGING_DIR_TARGET}/${prefix}/share/openvswitch/vswitch.xml > ${D}/srv/www/api/ops-restapi.json

      install -d ${D}/usr/share/opsplugins
      for plugin in $(find ${S}/opsplugins -name "*.py"); do \
        install -m 0644 ${plugin} ${D}/usr/share/opsplugins
      done
}


SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "restd.service"

inherit openswitch setuptools systemd pythonnative

FILES_${PN} += "/srv/www/api/ops-restapi.json \
                /etc/ssl/certs \
                /usr/share/opsplugins \
"
