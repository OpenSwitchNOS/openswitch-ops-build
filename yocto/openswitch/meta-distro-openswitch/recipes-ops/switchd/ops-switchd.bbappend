# Recipe variable overrides for feature/acl branch

SRC_URI = "git://git.openswitch.net/openswitch/ops-switchd;protocol=http;branch=feature/acl \
   file://switchd_bcm.service \
   file://switchd_sim.service \
   file://switchd_p4sim.service \
"

SRCREV = "887ecc9a2639defb54d16c52d20350b4f5283fab"
