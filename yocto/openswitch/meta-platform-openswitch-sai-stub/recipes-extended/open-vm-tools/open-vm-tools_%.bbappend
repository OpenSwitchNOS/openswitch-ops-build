# Copyright Mellanox Technologies, Ltd. 2001-2016.
# This software product is licensed under Apache version 2, as detailed in
# the COPYING file.

PR_append = "_sai_stub"

# Disable on containers
SYSTEMD_SERVICE_${PN} = ""
FILES_${PN} += "${systemd_unitdir}/system/vmtoolsd.service"
