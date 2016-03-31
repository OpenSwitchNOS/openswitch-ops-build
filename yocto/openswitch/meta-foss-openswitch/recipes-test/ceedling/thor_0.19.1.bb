SUMMARY = "Thor is a toolkit for building powerful command-line interfaces"
DESCRIPTION = "Thor is a simple and efficient tool for building self-documenting \
    command line utilities. It removes the pain of parsing command line \
    options, writing "USAGE:" banners, and can also be used as an alternative \
    to the Rake build tool. The syntax is Rake-like, so it should be familiar \
    to most Rake users"
HOMEPAGE = "http://whatisthor.com/"

inherit ruby

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=690cce21f4e069148a52834e1ecf352e"

SRCREV = "c74e1d34ef8b3270dcaa821fc1c7b38238929a27"

BRANCH = "master"
PV = "0.19.1"

SRC_URI = " \
    git://github.com/erikhuda/thor.git;branch=${BRANCH} \
    "
S = "${WORKDIR}/git"

RDEPENDS_${PN} = "git"

BBCLASSEXTEND = "native"
