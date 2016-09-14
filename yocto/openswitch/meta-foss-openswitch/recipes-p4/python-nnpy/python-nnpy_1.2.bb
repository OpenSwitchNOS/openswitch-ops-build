DESCRIPTION = "cffi-based Python bindings for nanomsg"
HOMEPAGE = "https://pypi.python.org/pypi/nnpy/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=72ee799854215969a05142429fa6c9a2"

SRCNAME = "nnpy"

SRC_URI = "https://files.pythonhosted.org/packages/source/n/${SRCNAME}/${SRCNAME}-${PV}.tar.gz"
SRC_URI[md5sum] = "9eb30c98c299d0d89a576f6083f0261a"
SRC_URI[sha256sum] = "c7465f72a43f2cc128d9ce6aae32cc112c59a011139e2eb8080c21dfdb229e72"

DEPENDS = "python-cffi nanomsg"

inherit setuptools

S = "${WORKDIR}/${SRCNAME}-${PV}"

# Cross-compilation support for nnpy.
# Values in site.cfg overrides package defaults.
do_compile_prepend() {
    echo "[DEFAULT]" > site.cfg
    echo "include_dirs = ${STAGING_INCDIR}/nanomsg/" >> site.cfg
    echo "library_dirs = ${STAGING_LIBDIR}/" >> site.cfg
    echo "host_library = ${STAGING_LIBDIR}/libnanomsg.so" >> site.cfg
}

BBCLASSEXTEND = "native"
