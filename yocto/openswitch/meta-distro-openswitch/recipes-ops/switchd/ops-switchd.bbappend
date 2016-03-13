SRC_URI = "git://git.openswitch.net/openswitch/ops-switchd;protocol=http;branch=feature/qos \
   file://switchd_bcm.service \
   file://switchd_sim.service \
   file://switchd_p4sim.service \
"

SRCREV = "${AUTOREV}"
