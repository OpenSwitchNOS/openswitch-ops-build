# Copyright (C) 2016 Hewlett Packard Enterprise Development LP

PR_append = "_sai_stub"

# Disable on containers
SYSTEMD_SERVICE_${PN} = ""
FILES_${PN} += "${systemd_unitdir}/system/vboxguest.service"
