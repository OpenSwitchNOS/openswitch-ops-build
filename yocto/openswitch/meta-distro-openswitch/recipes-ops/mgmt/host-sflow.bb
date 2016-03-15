SUMMARY = "Host sFlow agent"
HOMEPAGE = "http://www.sflow.net/"
LICENSE = "APL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3e784d8af30c680a4ddd4ddf341d9b92"

DEPENDS = "libcap"

SRC_URI = "git://github.com/sflow/host-sflow.git;protocol=https \
           file://0001-Fix-makefiles-to-support-cross-compiling.patch \
           file://host-sflow.service \
           "

SRCREV = "e0427b281883b204fc69d214d32d43f8ceae0413"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"
S = "${WORKDIR}/git"

EXTRA_OEMAKE += "SYSTEM_SLICE=yes WITH_SFLOWOVSD=no LIBVIRT=no XEN_DDK=no NVML=no DEBIAN=no REDHAT=no NFLOG=no"

do_compile_prepend() {
    export TARGET_SYSROOT=${STAGING_DIR_TARGET}
}

do_install() {
    make MAKEFLAGS= DEBIAN=no REDHAT=no install DESTDIR=${D}
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/host-sflow.service ${D}${systemd_unitdir}/system/
}

FILES_${PN} += "${systemd_unitdir}/system/host-sflow.service"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "host-sflow.service"
