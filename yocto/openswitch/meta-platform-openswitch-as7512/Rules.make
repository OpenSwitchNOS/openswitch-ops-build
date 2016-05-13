#  Copyright (C) 2016, Cavium, Inc.
#  All Rights Reserved.
#
# The contents of this software are proprietary and confidential to the
# Cavium, Inc.  No part of this program may be. photocopied, reproduced,
# or translated into another programming language. without prior written
# consent of the Cavium, Inc.

DISTRO_KERNEL_FILE = $(BASE_BZIMAGE_FILE)
DISTRO_FS_FILE = $(BASE_CPIO_FS_FILE)
DISTRO_FS_TARGET = openswitch-disk-image
ONIE_INSTALLER_RECIPE = openswitch-onie-installer
ONIE_INSTALLER_FILE = onie-installer-x86_64-as7512_32x

AS7512_ALL_TARGETS = onie-installer
ifneq ($(CONFIGURED_PLATFORM),multi)
all:: $(AS7512_ALL_TARGETS)
endif
