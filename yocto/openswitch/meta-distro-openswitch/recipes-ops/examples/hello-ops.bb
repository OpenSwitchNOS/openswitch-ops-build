SUMMARY = "OpenSwitch Training Example"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "file://hello.c \
   file://hello.h file://Makefile \
"

# Variable PN that resolves to the recipe name
# Variable PV that resolves to the recipe version
# In the recipe name the underscore separates the name from the version
# For example, you can have two recipes:
# hello_1.0.bb and hello_2.0.bb
# When invoking bitbake you only use the name

# Variable call PACKAGES, you don't need to specify, default values are OK
# PACKAGES = "${PN} ${PN}-dev ${PN}-dbg" 

# The FILES_ variable control what goes in what package
# FILES_${PN} ?= "/usr/bin /usr/sbin /usr/lib"

# If we were fetching a git repo, we had to specify:
# SRCREV = "<shasum or the tag>"

# WE have a variable called WORKDIR that maps to the working directory

# The S variable controls the location of the source
S = "${WORKDIR}"

# The B variable controls the location of the build
B = "${S}"

do_compile() {
    oe_runmake
}

# The D variable controls the destination of the installation
do_install() {
    oe_runmake install DESTDIR=${D}
}
