SUMMARY = "Set of sensor sysfs tools for linux"
SECTION = "base"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

inherit autotools-brokensep

DEPENDS_${PN} += "bison flex"
RDEPENDS_${PN} += "perl bash"

SENSORS_VERSION = "3-3-4"

SRC_URI = "https://github.com/groeck/lm-sensors/archive/V${SENSORS_VERSION}.zip \
           file://add_support_for_pwm_setting.patch \
           "

SRC_URI[md5sum] = "63bcb8fec823932c6b25f67b151766d9"
SRC_URI[sha256sum] = "2e1dd6d106a550dc2d1e39a8bef971e1c8a4f28920b13e6356306ed09f4e98e3"

S = "${WORKDIR}/lm-sensors-${SENSORS_VERSION}"

EXTRA_OEMAKE='PREFIX=${prefix}'

FILES_${PN} += "${prefix}/man"

do_configure_prepend() {
    sed -i 's/EXLDFLAGS/#EXLDFLAGS/g' ${S}/Makefile
}
