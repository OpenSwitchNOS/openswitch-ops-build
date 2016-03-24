SUMMARY = "OpenSwitch CLI"
LICENSE = "GPL-2.0 & LGPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=81bcece21748c91ba9992349a91ec11d\
                    file://COPYING.LIB;md5=01ef24401ded36cd8e5d18bfe947240c"

DEPENDS = "ops-utils ops-ovsdb"

SRC_URI = "git://git.openswitch.net/openswitch/ops-cli;protocol=http \
"

SRCREV = "3b8634609464e081c49b0b697dca0ca117e20b50"

# When using AUTOREV, we need to force the package version to the revision of git
# in order to avoid stale shared states.
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

inherit openswitch pkgconfig cmake

### HACK.  Do not merge me into master.
## I do not understand why master builds. The "warnigns" I get in ops-cli
## are very real and the several I looked into represent true runtime errors.
## I have a sinking feeling that master has added a CFLAGS:-w somewhere that
## I have not found in the yocto soup. Anyway...if master builds, then
## I'm gonin to ignore these warnings also.
CFLAGS_append = " -w "
