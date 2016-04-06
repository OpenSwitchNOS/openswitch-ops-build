# Recipe variable overrides for feature/acl branch

SRC_URI = "git://git.openswitch.net/openswitch/ops-switchd;protocol=http;branch=feature/acl \
   file://switchd_bcm.service \
   file://switchd_sim.service \
   file://switchd_p4sim.service \
"

SRCREV = "17a3ba4ab6cb698bd01b1943984553d6d0dc3ce2"
