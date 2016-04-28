DESCRIPTION = "Package groups for OpenSwitch applications"
LICENSE = "Apache-2.0"
PR = "r1"

#
# packages which content depend on MACHINE_FEATURES need to be MACHINE_ARCH
#
PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = ' \
            packagegroup-ops \
            packagegroup-ops-base \
            packagegroup-ops-min \
            packagegroup-ops-min-debug \
            packagegroup-ops-core \
            packagegroup-ops-config \
            '

PACKAGES += "${@bb.utils.contains("IMAGE_FEATURES", "ops-p4", "packagegroup-ops-p4", "", d)}"

#
# Following scheme is to address backward compatibility issue as not all
# platforms and features are integrated immediately into OpenSwitch Modular
# Configuration framework.
#
# Adding a new feature into OPS Mod Config framework:
# Include package associated with the feature to packagegroup-ops-base
#
# Porting an existing feature into OPS Mod Config framework:
# Remove package associated with the feature from packagegroup-ops-config
#
# Adding a new platform or porting an existing platform into OPS Mod Config framework:
# Include packagegroup-ops-config instead of packagegroup-ops-base
#
# Eventually, packagegroup-ops-base will go away
#
RDEPENDS_packagegroup-ops-base = "\
    os-release \
    i2c-tools \
    mtd-utils \
    gptfdisk \
    packagegroup-base-serial \
    lttng-tools lttng-modules lttng-ust babeltrace \
    kexec kdump \
    rsyslog \
    iproute2 dhcp-client\
    vim \
    tzdata-posix \
    valgrind \
    valgrind-memcheck \
    valgrind-helgrind \
    sudo \
    pwauth \
    shadow \
    cronie \
    auditd audispd-plugins audit-python \
    inetutils-ping inetutils-ping6 inetutils-hostname inetutils-ifconfig \
    inetutils-tftp inetutils-traceroute inetutils-ftp inetutils-telnet \
    iputils-traceroute6 \
    wget curl \
    xinetd \
    libcap-bin \
    ops-init \
    virtual/switchd \
    virtual/ops-switchd-switch-api-plugin \
    ops-ovsdb \
    ops-hw-config \
    ops-cfgd ops-fand ops-ledd ops-pmd ops-powerd ops-sysd ops-tempd \
    ops-dhcp-tftp \
    ops-intfd ops-lacpd ops-lldpd ops-vland ops-arpmgrd \
    ops-script-utils \
    ops-cli ops-restd ops-webui \
    ops-portd ops-quagga \
    ops-aaa-utils \
    ops-bufmond \
    ops-broadview \
    ops-mgmt-intf \
    dnsmasq \
    ops-checkmk-agent \
    ops-ansible \
    ops-ntpd \
    ops-supportability \
    strongswan \
"

RDEPENDS_packagegroup-ops-config = "\
    os-release \
    i2c-tools \
    mtd-utils \
    gptfdisk \
    packagegroup-base-serial \
    lttng-tools lttng-modules lttng-ust babeltrace \
    kexec kdump \
    rsyslog \
    iproute2 dhcp-client\
    vim \
    tzdata-posix \
    valgrind \
    valgrind-memcheck \
    valgrind-helgrind \
    sudo \
    pwauth \
    shadow \
    cronie \
    auditd audispd-plugins audit-python \
    inetutils-ping inetutils-ping6 inetutils-hostname inetutils-ifconfig \
    inetutils-tftp inetutils-traceroute inetutils-ftp inetutils-telnet \
    iputils-traceroute6 \
    wget curl \
    xinetd \
    libcap-bin \
    ops-init \
    virtual/switchd \
    virtual/ops-switchd-switch-api-plugin \
    ops-ovsdb \
    ops-hw-config \
    ops-cfgd ops-fand ops-ledd ops-pmd ops-powerd ops-sysd ops-tempd \
    ops-dhcp-tftp \
    ops-intfd ops-lacpd ops-lldpd ops-vland ops-arpmgrd \
    ops-script-utils \
    ops-cli ops-restd ops-webui \
    ops-portd ops-quagga \
    ops-aaa-utils \
    ops-mgmt-intf \
    dnsmasq \
    ops-checkmk-agent \
    ops-ansible \
    ops-supportability \
    strongswan \
"

RDEPENDS_packagegroup-ops-base_append_arm = "\
    u-boot-fw-utils \
    t32server\
    "

RDEPENDS_packagegroup-ops-min = "\
    python \
    python-pyroute2 \
    yaml-cpp \
    libevent \
    util-linux \
    iptables \
    "

RDEPENDS_packagegroup-ops-min-debug = "\
    file strace ldd tcpdump gdb gdbserver eglibc-thread-db \
    iperf ethtool tcf-agent nfs-utils-client \
    "

RDEPENDS_packagegroup-ops-core = "\
    "

RDEPENDS_packagegroup-ops-p4 = "\
    ops-switchd-p4switch-plugin \
    ops-p4dp \
    "
