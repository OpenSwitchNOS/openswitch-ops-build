# Recipe variable overrides for feature/acl branch

SRC_URI = "git://git.openswitch.net/openswitch/ops-openvswitch;protocol=http;branch=feature/acl \
   file://ovsdb-server.service \
   file://partial-map-updates.patch \
   file://on-demand-fetching.patch \
   file://compound-indexes.patch \
   file://idl_tracking_python.patch \
"

SRCREV = "7860929eb0e457cd53bd1c2a9ffa611e2c727d97"
