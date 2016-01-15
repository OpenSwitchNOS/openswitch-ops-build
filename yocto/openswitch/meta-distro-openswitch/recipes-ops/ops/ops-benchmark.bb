SUMMARY = "OVSDB and Datapath Benchmarking"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE-2.0.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS = "ops-ovsdb"

SRC_URI = "git://git.openswitch.net/openswitch/ops-benchmark;protocol=http"

SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git"

inherit cmake

# Uncomment the following line if you want to deploy the benchmark binary 
# as part of the image
#FILES_${PN} = "${bindir}/ovsdb-benchmark"
