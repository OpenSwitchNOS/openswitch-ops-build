#!/bin/bash
# Script used to boot genericx86-64 platforms using KVM
# Usage: tools/bin/boot.sh <root extract directory>
# Root extract directory is used to extract the root filesystem to use for the
# machine. Do not share the same directory between instances.

RAM=1024
CORES=2
KERNEL=build/tmp/deploy/images/genericx86-64/bzImage-genericx86-64.bin
KERNELARGS="ro rootfstype=9p rootflags=trans=virtio console=ttyS0,115200n8"
ROOTTAR="images/openswitch-disk-image-genericx86-64.tar"
EXTRACTDIR="$1"
NETSCRIPT="$(dirname $0)/qemu-ifup.sh"

if [ -z "$1" ]; then
  echo "Usage: $0 <root extract directory>"
  exit 1
fi

if [ "$EUID" != "0" ]; then
  # Since we're going to be extracting a tarball with permissions we want to
  # be root. We will also create network interfaces, which usually requires
  # root.
  echo "Script needs to be run as root"
  exit 1
fi

if [ ! -w "/dev/kvm" ]; then
  echo "/dev/kvm is not found or not writable."
  echo "Make sure your platform is supported by KVM and that you have"
  echo "permissions to use KVM."
  exit 1
fi

if [ ! -r "${ROOTTAR}" ]; then
  echo "Unable to read ${ROOTTAR} - has the platform been built?"
  exit 1
fi

if [ -e "${EXTRACTDIR}" ]; then
  echo -n "Root extract directory already exists, will use that as root."
else
  echo "Extracting root..."
  mkdir -p "${EXTRACTDIR}"
  tar -xpf "${ROOTTAR}" -C "${EXTRACTDIR}"
fi

echo "Booting OpenSwitch..."
kvm -m "${RAM}" -nographic -nodefaults -serial mon:stdio \
  -cpu core2duo -smp "${CORES}" \
  -kernel "${KERNEL}" -netdev type=tap,id=net0,script="${NETSCRIPT}" \
  -device virtio-net-pci,netdev=net0 \
  -fsdev local,id=r,path="${EXTRACTDIR}",security_model=passthrough \
  -device virtio-9p-pci,fsdev=r,mount_tag=/dev/root \
  -append "${KERNELARGS}"
