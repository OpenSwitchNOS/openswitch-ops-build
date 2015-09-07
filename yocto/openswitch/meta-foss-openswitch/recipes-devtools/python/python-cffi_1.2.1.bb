SUMMARY = "Foreign Function Interface for Python calling C code."
HOMEPAGE = "https://pypi.python.org/pypi/cffi"

SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM="file://LICENSE;md5=5677e2fdbf7cdda61d6dd2b57df547bf"

SRCNAME = "cffi"
DEPENDS ="python-pycparser"
RDEPENDS_${PN} ="python-pycparser"
SRC_URI[md5sum] = "faca1e420e80433db409bb1bdd0a3f45"
SRC_URI[sha256sum] = "eab571deb0a152e2f53c404c08a94870a131526896cad08cd43bf86ce3771e3d"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit pypi

CLEANBROKEN = "1"
