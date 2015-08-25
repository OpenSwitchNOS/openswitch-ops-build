# Copyright (C) 2015 Hewlett Packard Enterprise Development LP

# On generic X86 build (which runs on virtual platforms),
# serial consoles are absent. There is no need to run
# getty on those platforms.
do_install_append() {
    /bin/rm -rf ${D}$(sysconfdir)/system/getty.target.wants
}
