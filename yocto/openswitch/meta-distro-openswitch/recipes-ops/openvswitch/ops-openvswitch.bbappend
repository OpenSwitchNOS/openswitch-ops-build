# Recipe variable overrides for feature/acl branch

SRC_URI = "git://git.openswitch.net/openswitch/ops-openvswitch;protocol=http;branch=feature/acl \
   file://ovsdb-server.service \
   file://enable-jemalloc-ovsdb-server.patch \
   file://partial-map-updates.patch \
   file://on-demand-fetching.patch \
   file://compound-indexes.patch \
"

SRCREV = "623167323abeb42a99a171943e9f9709329f5086"
