# Recipe variable overrides for feature/acl branch

SRC_URI = "git://git.openswitch.net/openswitch/ops-openvswitch;protocol=http;branch=feature/acl \
   file://ovsdb-server.service \
   file://enable-jemalloc-ovsdb-server.patch \
   file://partial-map-updates.patch \
   file://on-demand-fetching.patch \
   file://compound-indexes.patch \
"

SRCREV = "52e9a71a587ca35e244e9d209212f576077b86e9"
