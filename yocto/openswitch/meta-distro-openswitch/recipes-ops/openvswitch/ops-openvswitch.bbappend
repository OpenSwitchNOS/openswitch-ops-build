SRC_URI = "git://git.openswitch.net/openswitch/ops-openvswitch;protocol=http;branch=feature/qos \
   file://ovsdb-server.service \
   file://partial-map-updates.patch \
   file://on-demand-fetching.patch \
   file://compound-indexes.patch \
"

SRCREV = "${AUTOREV}"
