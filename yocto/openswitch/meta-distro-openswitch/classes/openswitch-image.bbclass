inherit core-image extrausers

EXTRA_USERS_PARAMS = "\
         useradd -N -M -r opsd; \
         usermod -s /bin/false opsd;\
         usermod -g ovsdb-client opsd;\
         useradd -N -P netop netop; \
         useradd -N -P admin admin; \
         usermod -g ops_admin admin;\
         usermod -g ops_netop netop;\
         usermod -G ovsdb-client netop;\
         usermod -s /bin/bash admin;\
         usermod -s /usr/bin/vtysh netop;\
         useradd -N -M -r opsrestd; \
         usermod -g ops_restd opsrestd; \
         usermod -a -G ovsdb-client,shadow,ops_netop opsrestd;\
         "

IMAGE_FEATURES += "ssh-server-openssh"

IMAGE_GEN_DEBUGFS = "1"

ROOTFS_POSTPROCESS_COMMAND += "change_file_attributes;"

change_file_attributes () {
    chmod 0440 ${IMAGE_ROOTFS}/etc/shadow
    chgrp shadow ${IMAGE_ROOTFS}/etc/shadow
}
