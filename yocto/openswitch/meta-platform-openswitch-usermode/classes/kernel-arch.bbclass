valid_archs = "alpha cris ia64 \
               i386 x86 \
               m68knommu m68k ppc powerpc powerpc64 ppc64  \
               sparc sparc64 \
               arm aarch64 \
               m32r mips \
               sh sh64 um h8300   \
               parisc s390  v850 \
               avr32 blackfin \
               microblaze"

def map_kernel_arch(a, d):
    import re

    valid_archs = d.getVar('valid_archs', True).split()
    if d.getVar('USERMODE_LINUX', True):        return 'um'

    if   re.match('(i.86|athlon|x86.64)$', a):  return 'x86'
    elif re.match('armeb$', a):                 return 'arm'
    elif re.match('aarch64$', a):               return 'arm64'
    elif re.match('aarch64_be$', a):            return 'arm64'
    elif re.match('mips(el|64|64el)$', a):      return 'mips'
    elif re.match('p(pc|owerpc)(|64)', a):      return 'powerpc'
    elif re.match('sh(3|4)$', a):               return 'sh'
    elif re.match('bfin', a):                   return 'blackfin'
    elif re.match('microblazee[bl]', a):        return 'microblaze'
    elif a in valid_archs:                      return a
    else:
        bb.error("cannot map '%s' to a linux kernel architecture" % a)

def get_arch(d):
  if d.getVar('PN', True) != 'linux-libc-headers':
    return 'um'
  return map_kernel_arch(d.getVar('TARGET_ARCH', True), d)

export ARCH = "${@get_arch(d)}"

KERNEL_CC ?= "${BUILD_CC}"
KERNEL_LD ?= "${BUILD_LD}"
KERNEL_AR ?= "${BUILD_AR}"
