# Recipe variable overrides for feature/acl branch

SRC_URI = "git://git.openswitch.net/openswitch/ops-openvswitch;protocol=http;branch=feature/acl \
   file://ovsdb-server.service \
   file://enable-jemalloc-ovsdb-server.patch \
   file://partial-map-updates.patch \
   file://on-demand-fetching.patch \
   file://compound-indexes.patch \
"

SRCREV = "714194488b13bacf504f1b5b0412807cd1eef7d8"
