# Recipe variable overrides for feature/acl branch

SRC_URI = "git://git.openswitch.net/openswitch/ops-switchd;protocol=http;branch=feature/acl \
   file://switchd_bcm.service \
   file://switchd_sim.service \
   file://switchd_p4sim.service \
"

SRCREV = "107ce4f327facf7fa634a57cacf659f99d44967d"
