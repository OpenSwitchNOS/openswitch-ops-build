SUMMARY = "A virtual machine image of OpenSwitch"

LICENSE = "Apache-2.0"

IMAGE_ROOTFS_SIZE = "1015808"

# Do a quiet boot with limited console messages
APPEND += "quiet"

DEPENDS = "tar-native"
IMAGE_FSTYPES = "ext4 tar.gz"

IMAGE_CMD_ext4_append () {
	# We don't need to reserve much space for root, 0.5% is more than enough
	tune2fs -m 0.5 ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.ext4
}

inherit openswitch-image
