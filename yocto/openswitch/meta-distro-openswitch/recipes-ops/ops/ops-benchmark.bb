SUMMARY = "OVSDB and Datapath Benchmarking"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE-2.0.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS = "ops-ovsdb"

SRC_URI = "git://git.openswitch.net/openswitch/ops-benchmark;protocol=http;branch=feature/benchmark"

SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git"

inherit cmake

FILES_${PN} = "${bindir}/ovsdb-benchmark"
