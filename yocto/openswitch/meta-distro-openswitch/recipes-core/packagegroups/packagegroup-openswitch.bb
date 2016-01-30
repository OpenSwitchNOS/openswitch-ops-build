DESCRIPTION = "Package groups for OpenSwitch applications"
LICENSE = "Apache-2.0"
PR = "r1"

SRC_URI = "file://${TOPDIR}/.ops-config"

#
# packages which content depend on MACHINE_FEATURES need to be MACHINE_ARCH
#
PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup
inherit openswitch-config

#
# ANIK TODO: Without this flag, anything added to PACKAGES is not taking effect.
#            Understand what this flag is - nothing mentioned in Yocto docs.
#
#PACKAGEGROUP_DISABLE_COMPLEMENTARY = "1"

PROVIDES = "${PACKAGES}"
PACKAGES = ' \
            packagegroup-ops \
            packagegroup-ops-base \
            packagegroup-ops-min \
            packagegroup-ops-min-debug \
            packagegroup-ops-core \
            '

PACKAGES += "${@bb.utils.contains("IMAGE_FEATURES", "ops-p4", "packagegroup-ops-p4", "", d)}"

#
# ANIK: Include packages based on included features
#       Features are controlled via Kconfig mechanism
#
#       If a feature involves multiple OPS Repos, package groups can
#       be created out of individual packages.
#
#PACKAGES =+ "${@bb.utils.contains('IMAGE_FEATURES','CLI','packagegroup-ops-cli','',d)}"
#PACKAGES =+ "${@bb.utils.contains('IMAGE_FEATURES','VLAN','packagegroup-ops-vlan','',d)}"
#PACKAGES =+ "${@bb.utils.contains('IMAGE_FEATURES','ANSIBLE','packagegroup-ops-ansible','',d)}"

#RDEPENDS_packagegroup-ops-cli = "ops-cli"
#RDEPENDS_packagegroup-ops-vlan = "ops-vland"
#RDEPENDS_packagegroup-ops-ansible = "ops-ansible"

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
    ops-intfd ops-lacpd ops-lldpd ops-arpmgrd \
    ops-script-utils \
    ops-restd lighttpd \
    ops-portd ops-quagga \
    ops-aaa-utils \
    ops-bufmond \
    ops-broadview \
    ops-mgmt-intf \
    dnsmasq \
    ops-checkmk-agent \
    ops-ntpd \
    ops-supportability \
    strongswan \
    ${@bb.utils.contains('IMAGE_FEATURES','CLI','ops-cli','',d)} \
    ${@bb.utils.contains('IMAGE_FEATURES','VLAN','ops-vland','',d)} \
    ${@bb.utils.contains('IMAGE_FEATURES','ANSIBLE','ops-ansible','',d)} \
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
