# This class is meant to be used in projects that will use Ceedling as its
# C Unit Test framework.

inherit ruby-base

DEPENDS_append = "ceedling-native "

PROJECT_HOME= "${EXTERNALSRC}/tests/ops-unit-test"
C_FLAGS="${HOST_CC_ARCH} ${TOOLCHAIN_OPTIONS} ${CFLAGS}"
C_LINK_FLAGS="${HOST_CC_ARCH} ${TOOLCHAIN_OPTIONS} ${CPPFLAGS} ${LDFLAGS}"

# Helper functions
ceedling_generate_env() {
    ceedling_bin=$1
    proj_dir=$2

    # Create the tests dir
    mkdir $proj_dir
    # Use ceedling to lay out a new project
    $ceedling_bin new $proj_dir

    [ -e $proj_dir/src ] && rm -r $proj_dir/src
    ln -s ${EXTERNALSRC}/src $proj_dir/src
    [ -e $proj_dir/include ] && rm -r $proj_dir/include
    ln -s ${EXTERNALSRC}/include $proj_dir/include
    [ ! -h $proj_dir/vendor ] && rm -rf $proj_dir/vendor
    [ ! -e $proj_dir/build/.gitkeep ] && touch $proj_dir/build/.gitkeep
    [ ! -e $proj_dir/test/support/.gitkeep ] && touch $proj_dir/test/support/.gitkeep
    # TODO remove project.yml once we build it from the template.
}

ceedling_write_project_file_template() {
   tohere=$1
    cat > ${tohere}/project_template.yml <<EOF
#
# File: project_template.yml
#
# Authors: Pablo Barrantes <pbarrantes@hpe.com
#          Damien Keehn <damien.keehn@hpe.com
#
# Description:
#   Template file to generate a project.yml by substituting the values inside
#   square brackets "[]".
#
# WARNING: This file is autogenerated. Edit if you know what you're doing. Values
#          in square brackets will be replaced by ceedling class.

:project:
    :use_exceptions: TRUE
    :use_mocks: TRUE
    :use_preprocessor: TRUE
    :use_auxiliary_dependencie: FALSE
    :build_root: [BUILD_DIR]

:paths:
    :test:
    - [TESTS_DIR]
    :source:
    - [SRC_DIR]
    :include:
    - [INCL_DIR]

:tools:
    :test_compiler:
    :executable: [COMPILER]
    :arguments:
        - -c
        - "${1}"
        - -o "${2}"
        - -D$: COLLECTION_DEFINES_TEST_AND_VENDOR
        - -I"$": COLLECTION_PATHS_TEST_SUPPORT_SOURCE_INCLUDE_VENDOR

  :test_linker:
    :executable: [LINKER]
    :arguments:
        - ${1}
        - -o "${2}"

:flags:
    :test:
        :compile:
            :*:
               - [C_FLAGS]  # Add to compilation of all files
        :link:
            :*:
               - [LD_FLAGS] # Add to linking of all files

EOF
}

ceedling_update_vendor_path() {
    # Point to shared vendor directory location
    project_home=$1
    ceedlingRoot=$2

    lib=$project_home/vendor/ceedling/lib
    vendor=$project_home/vendor/ceedling/vendor
    plugins=$project_home/vendor/ceedling/plugins
    docs=$project_home/vendor/ceedling/docs

    mkdir -p $project_home/vendor/ceedling

    # Recreate all links
    [ -e $lib ] && rm $lib
    ln -s $ceedlingRoot/lib $lib
    [ -e $vendor ] && rm $vendor
    ln -s $ceedlingRoot/vendor $vendor
    [ -e $plugins ] && rm $plugins
    ln -s $ceedlingRoot/plugins $plugins
    [ -e $docs ] && rm $docs
    ln -s $ceedlingRoot/docs $docs

    # TODO add .gitignore to prevent links from getting checked in.
}

def get_c_linker(d):
    import re
    found_linker = "SOMETHING FAILED!"

    linker = d.getVar('LD', True)
    r = re.compile("[_0-9a-z-]+ ")
    m = r.match(str(linker))
    if m:
        return m.group()
# End of get_c_linker

def ceedling_create_project_file(d):
    # Define some constants
    PROJECT_TEMPLATE_FILENAME="project_template.yml"
    PROJECT_OUT_FILENAME="project.yml"

    # Get required datastore variables
    proj_root = d.getVar('PROJECT_HOME', True)
    c_compiler = d.getVar('OECMAKE_C_COMPILER', True)
    c_linker = get_c_linker(d)
    c_flags = d.getVar('C_FLAGS', True)
    c_linker_flags = d.getVar('C_LINK_FLAGS', True)

    # Get the template file data
    projectTemplateFile = open(proj_root + "/" + PROJECT_TEMPLATE_FILENAME, "r")
    if projectTemplateFile.closed:
        bb.plain("Couldn't open project template file")
        return -1
    projectOutData = projectTemplateFile.read()
    projectTemplateFile.close()

    # Replace the parameters in the template
    projectOutData = projectOutData.replace("[BUILD_DIR]", proj_root + "/build")
    projectOutData = projectOutData.replace("[SRC_DIR]", proj_root + "/src")
    projectOutData = projectOutData.replace("[INCL_DIR]", proj_root + "/include")
    projectOutData = projectOutData.replace("[TESTS_DIR]", proj_root + "/test")
    projectOutData = projectOutData.replace("[COMPILER]", str(c_compiler))
    projectOutData = projectOutData.replace("[LINKER]", str(c_linker))
    projectOutData = projectOutData.replace("[C_FLAGS]", str(c_flags))
    projectOutData = projectOutData.replace("[LD_FLAGS]", str(c_linker_flags))

    # Write the result to the project.yml
    projectOutFile = open(proj_root + "/" + PROJECT_OUT_FILENAME, "w+")
    if projectOutFile.closed:
        bb.plain("Couldn't open project file")
        return -1
    projectOutFile.write(projectOutData)
    projectOutFile.close()

    return 0
# End of ceedling_create_project_file

# Ceedling additional tasks
addtask generate_project_if_none before do_compile

ceedling_do_generate_project_if_none() {
    # Ceedling paths
    CEEDLING_PV=$(basename `find $GEM_HOME/gems -maxdepth 1 -type d -name "ceedling*" | tail -n 1` | sed s/ceedling-//g);
    CEEDLING_GEM_PATH="${GEM_HOME}/gems/ceedling-${CEEDLING_PV}"
    CEEDLING_BIN=${CEEDLING_GEM_PATH}/bin/ceedling
    # Location of test dir. TODO get official name
    PROJECT_HOME="${EXTERNALSRC}/tests/ops-unit-test"

    if [ ! -e ${EXTERNALSRC} ]; then
        # The code is NOT checked out. return all OK
        return 0
    fi

    if [ ! -e ${CEEDLING_BIN} ]; then
        echo "Could not find ceedling binary in $CEEDLING_GEM_PATH/bin"
        return -1
    fi

    if [ ! -e ${PROJECT_HOME} ]; then
        # Create the test env
        ceedling_generate_env ${CEEDLING_BIN} ${PROJECT_HOME}
        # Create project template file
        ceedling_write_project_file_template ${PROJECT_HOME}
    fi

    ceedling_update_vendor_path $PROJECT_HOME $CEEDLING_GEM_PATH
    return ${@ceedling_create_project_file(d)}
}

EXPORT_FUNCTIONS do_generate_project_if_none
