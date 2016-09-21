
#NOISO = "1"

SYSLINUX_ROOT ?= "root=PARTUUID=${DISK_SIGNATURE}-02 "
SYSLINUX_PROMPT ?= "0"
SYSLINUX_TIMEOUT ?= "1"
SYSLINUX_LABELS = "boot"
LABELS_append = " ${SYSLINUX_LABELS} "

# need to define the dependency and the ROOTFS for directdisk
do_bootdirectdisk[depends] += "${PN}:do_rootfs"
ROOTFS ?= "${DEPLOY_DIR_IMAGE}/${IMAGE_BASENAME}-${MACHINE}.ext4"

# creating QCOW2 relies on having a live hddimg so ensure we
# inherit it here.
#inherit image-live
inherit boot-directdisk

IMAGE_TYPEDEP_qcow2 = "ext4"
IMAGE_TYPES_MASKED += "qcow2"

create_qcow2_image () {
#	qemu-img convert -o subformat=streamOptimized -O qcow2 \
#	qemu-img convert -c -O qcow2 \
	qemu-img convert -O qcow2 \
		${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.hdddirect \
		${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.qcow2
	ln -sf ${IMAGE_NAME}.qcow2 ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}.qcow2
}

python do_qcow2img() {
        bb.build.exec_func('create_qcow2_image', d)
}

#addtask qcow2img after do_bootimg before do_build
addtask qcow2img after do_bootdirectdisk before do_build

do_qcow2img[depends] += "qemu-native:do_populate_sysroot"
