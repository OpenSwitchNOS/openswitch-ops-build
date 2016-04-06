# Recipe variable overrides for feature/acl branch

SRC_URI = "git://git.openswitch.net/openswitch/ops-openvswitch;protocol=http;branch=feature/acl \
   file://ovsdb-server.service \
   file://partial-map-updates.patch \
   file://on-demand-fetching.patch \
   file://compound-indexes.patch \
   file://idl_tracking_python.patch \
"

SRCREV = "cb6110c71b46da14b0246f185ce342a5e2b76933"
