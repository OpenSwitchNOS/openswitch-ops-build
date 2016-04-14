# Recipe variable overrides for feature/acl branch

SRC_URI = "git://git.openswitch.net/openswitch/ops-switchd;protocol=http;branch=feature/stats_update \
          file://switchd_bcm.service \
          file://switchd_sim.service \
          file://switchd_p4sim.service \
          file://switchd_xpliant.service \
"

SRCREV = "d0ecd8abb0cc876fdb7e42b5fb6fa0238a671514"
