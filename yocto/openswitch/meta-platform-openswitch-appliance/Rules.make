# Copyright (C) 2015 Hewlett Packard Enterprise Development LP
# All Rights Reserved.
#
# The contents of this software are proprietary and confidential to the
# Hewlett-Packard Development Company, L. P.  No part of this program may be.
# photocopied, reproduced, or translated into another programming language.
# without prior written consent of the Hewlett-Packard Development Co., L. P.

DISTRO_KERNEL_FILE = $(BASE_BZIMAGE_FILE)
DISTRO_FS_FILE = $(BASE_TARGZ_FS_FILE)
DISTRO_EXTRA_FS_FILES = $(BASE_OVA_FILE)
DISTRO_FS_TARGET = openswitch-appliance-image

# Used for LXC container images configuration
LXC_MACHINE_CONFIG_SCRIPT=$(BUILD_ROOT)/yocto/openswitch/meta-platform-$(DISTRO)-$(CONFIGURED_PLATFORM)/lxc.sh
export LXC_MACHINE_CONFIG_SCRIPT

# For this platform we create a itb image that includes a kernel, fs and dtb
all:: fs _kernel_links

