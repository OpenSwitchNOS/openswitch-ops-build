# OpenSwitch repos are fetched from git, so creating tarballs for them
# only ruins the origin remotes in the devevn
BB_GENERATE_MIRROR_TARBALLS = "0"

# Most of the code requires this flag right now, otherwise the
# structures in OVS get corrupted. This needs to be removed soon
CFLAGS += "-DHALON"
CFLAGS += "-DOPS"

# Do builds with debug mode by default
DEBUG_BUILD = "1"

# Enable profiling for devenv recipes (meaning they are in external src)
def enable_devenv_profiling(d):
    externalsrc = d.getVar('EXTERNALSRC', True)
    if externalsrc:
        return bb.utils.contains('MACHINE_FEATURES', 'ops-devenv-profiling', '-p', '',d)
    return ""

# Debug flags is used by DEBUG_OPTIMIZATION that is used by SELECTED_OPTIMIZATION when DEBUG_BUILD is 1
DEBUG_FLAGS = "${@enable_devenv_profiling(d)}"

# Support for static analysis using HP Fortify
inherit python-dir

#-Dcom.fortify.sca.compilers.${HOST_PREFIX}gcc=com.fortify.sca.util.compilers.GccCompiler -Dcom.fortify.sca.compilers.${HOST_PREFIX}g++=com.fortify.sca.util.compilers.GppCompiler
FORTIFY_PARAMETERS = "-b ${PN} -python-path ${STAGING_DIR_TARGET}${PYTHON_SITEPACKAGES_DIR}  "

def get_static_analysis_cmd(d):
    externalsrc = d.getVar('EXTERNALSRC', True)
    if externalsrc:
        return bb.utils.contains('MACHINE_FEATURES', 'ops-devenv-fortify-sca', 'sourceanalyzer ${FORTIFY_PARAMETERS} ', '',d)
    return ""

do_generate_sca_wrappers() {
    if which sourceanalyzer > /dev/null ; then
        sourceanalyzer -b ${PN} --clean
    fi

	cat > ${WORKDIR}/fortify-gcc <<EOF
sourceanalyzer ${FORTIFY_PARAMETERS} ${HOST_PREFIX}gcc \$@
EOF
    chmod +x ${WORKDIR}/fortify-gcc
cat > ${WORKDIR}/fortify-g++ <<EOF
sourceanalyzer ${FORTIFY_PARAMETERS} ${HOST_PREFIX}g++ \$@
EOF
    chmod +x ${WORKDIR}/fortify-g++
cat > ${WORKDIR}/fortify-ar <<EOF
sourceanalyzer ${FORTIFY_PARAMETERS} ${HOST_PREFIX}ar \$@
EOF
    chmod +x ${WORKDIR}/fortify-ar
cat > ${WORKDIR}/fortify-ld <<EOF
sourceanalyzer ${FORTIFY_PARAMETERS} ${HOST_PREFIX}ld \$@
EOF
    chmod +x ${WORKDIR}/fortify-ld
}

addtask generate_sca_wrappers after do_patch before do_configure
#addtask generate_sca_wrappers after do_patch before do_configure

def get_cmake_c_compiler(d):
    externalsrc = d.getVar('EXTERNALSRC', True)
    if externalsrc:
        return bb.utils.contains('MACHINE_FEATURES', 'ops-devenv-fortify-sca', "${WORKDIR}/fortify-gcc", "${HOST_PREFIX}gcc",d)
    return "${HOST_PREFIX}gcc"

def get_cmake_cxx_compiler(d):
    externalsrc = d.getVar('EXTERNALSRC', True)
    if externalsrc:
        return bb.utils.contains('MACHINE_FEATURES', 'ops-devenv-fortify-sca', "${WORKDIR}/fortify-g++", "${HOST_PREFIX}g++",d)
    return "${HOST_PREFIX}g++"

export CC = "${@get_static_analysis_cmd(d)}${CCACHE}${HOST_PREFIX}gcc ${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}"
export CXX = "${@get_static_analysis_cmd(d)}${CCACHE}${HOST_PREFIX}g++ ${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}"
export FC = "${@get_static_analysis_cmd(d)}${CCACHE}${HOST_PREFIX}gfortran ${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}"
export CPP = "${@get_static_analysis_cmd(d)}${HOST_PREFIX}gcc -E${TOOLCHAIN_OPTIONS} ${HOST_CC_ARCH}"
OECMAKE_C_COMPILER = "${@get_cmake_c_compiler(d)}"
OECMAKE_CXX_COMPILER = "${@get_cmake_cxx_compiler(d)}"

# Do cmake builds in debug mode
EXTRA_OECMAKE+="-DCMAKE_BUILD_TYPE=Debug"
# Enable simulation flag for cmake-based projects
EXTRA_OECMAKE+="${@bb.utils.contains('MACHINE_FEATURES', 'ops-container', '-DPLATFORM_SIMULATION=ON', '',d)}"
# Provide cmake-based projects endianness information
EXTRA_OECMAKE+="${@base_conditional('SITEINFO_ENDIANNESS', 'le', '-DCPU_LITTLE_ENDIAN=ON', '-DCPU_BIG_ENDIAN=ON', d)}"

# Add debug directory for packages
PACKAGE_DEBUG_SPLIT_STYLE??="debug-file-directory"

# For debugging/development purposes on devtool
EXTERNALSRC_BUILD??="${S}/build"

inherit siteinfo
