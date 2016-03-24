# User-Mode Linux

Configuration layer for the user-mode platform.

## Dependencies

You will need `uml-utilities` if you want to use auto-assigned TAP devices,
as is recommended. Specifically you need `/usr/lib/uml/uml_net`.

Note: On Debian-based platforms you also need to be in the group `uml-net`.

## How-to

When the platform has finished building, execute it like this:
```
images/kernel-usermode.bin mem=512m \
  ubda=images/openswitch-usermode-image-usermode.ext4 \
  eth0=tuntap,,,172.17.0.1 ip=172.17.0.2::172.17.0.1:255.255.255.252
```

The system will be reachable on 172.17.0.2.

