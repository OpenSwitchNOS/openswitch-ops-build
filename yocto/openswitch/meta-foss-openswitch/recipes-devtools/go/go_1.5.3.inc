require go.inc

SRC_URI = "${SRC_URI_PREFIX}/go${PV}.src.tar.gz;name=go_1_5;subdir=go-${PV}"
SRC_URI[go_1_5.md5sum] = "80a0eac7ab750b01b3f7096a1d4667b8"
SRC_URI[go_1_5.sha256sum] = "754e06dab1c31ab168fc9db9e32596734015ea9e24bc44cae7f237f417ce4efe"

LIC_FILES_CHKSUM = "file://${WORKDIR}/go-${PV}/go/LICENSE;md5=591778525c869cdde0ab5a1bf283cd81"

S = "${WORKDIR}/go-${PV}/go"

DEPENDS = "go-bootstrap"

SRC_URI += "\
        file://Fix-ccache-compilation-issue.patch \
        file://fix-cross-compilation.patch \
        "

do_install() {
  GOROOT_FINAL="${D}${libdir}/go"

  install -d "${D}${bindir}" "${GOROOT_FINAL}"
  tar -C "${WORKDIR}/go-${PV}/go" -cf - bin lib src pkg test |
  tar -C "${GOROOT_FINAL}" -xpf -
  mv "${GOROOT_FINAL}/bin/"* "${D}${bindir}/"
}

## TODO: implement do_clean() and ensure we actually do rebuild super cleanly
