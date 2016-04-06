# Recipe variable overrides for feature/acl branch

SRC_URI = "git://git.openswitch.net/openswitch/ops-switchd;protocol=http;branch=feature/acl \
   file://switchd_bcm.service \
   file://switchd_sim.service \
   file://switchd_p4sim.service \
"

SRCREV = "4c623aa06f29a18562d065c4ddc93f242d87bbed"
