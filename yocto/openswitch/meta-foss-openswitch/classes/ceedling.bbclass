# This class is meant to be used in projects that will use Ceedling as its
# C Unit Test framework.

DEPENDS_prepend = "ceedling-native "

inherit ruby-base

# Helper functions
ceedling_generate_env() {
    ceedling_bin=$1
    proj_dir=$2

    # Create the tests dir
    mkdir $proj_dir
    # Use ceedling to lay out a new project
    $ceedling_bin new $proj_dir

    [ -e $proj_dir/src ] && rm -r $proj_dir/src
    ln -s ${S}/src $proj_dir/src
    [ -e $proj_dir/include ] && rm -r $proj_dir/include
    ln -s ${S}/include $proj_dir/include
    [ ! -h $proj_dir/vendor ] && rm -rf $proj_dir/vendor
    [ ! -e $proj_dir/build/.gitkeep ] && touch $proj_dir/build/.gitkeep
    [ ! -e $proj_dir/test/support/.gitkeep ] && touch $proj_dir/test/support/.gitkeep
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
    :use_exceptions: FALSE
    :use_mocks: TRUE
    :use_preprocessor: TRUE
    :use_auxiliary_dependencies: FALSE
    :build_root: [BUILD_DIR]

:paths:
    :test:
        - [TESTS_DIR]
    :source:
        - [SRC_DIR]
    :include:
        - [INCL_DIR]
        - [STAGING_INCDIR_NATIVE]
        - [STAGING_INCDIR]

:tools:
    :test_compiler:
        :executable: [COMPILER]
        :arguments:
            - -c
            - \${1}
            - -o \${2}
            - -D$: COLLECTION_DEFINES_TEST_AND_VENDOR
            - -I"$": COLLECTION_PATHS_TEST_SUPPORT_SOURCE_INCLUDE_VENDOR

    :test_linker:
        :executable: [LINKER]
        :arguments:
            - \${1}
            - -o \${2}

:flags:
    :test:
        :compile:
            :*:
                - [C_FLAGS]
        :link:
            :*:
                - [LD_FLAGS]

:plugins:
    :load_paths:
        - vendor/ceedling/plugins
    :enabled:
        - stdout_pretty_tests_report
        - module_generator
        - gcov

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

    if [ -e $project_home/.gitignore ]; then
        vendor_ignored=`cat $project_home/.gitignore | grep vendor`
        if [ -z $vendor_ignored ]; then
            echo "vendor" >> $project_home/.gitignore;
        fi
    else
        echo "vendor" > $project_home/.gitignore
    fi
}

ceedling_create_project_file () {
    # Define some constants
    PROJECT_HOME=$1
    PROJECT_TEMPLATE_PATH="$PROJECT_HOME/project_template.yml"
    PROJECT_OUT_PATH="$PROJECT_HOME/project.yml"

    # Copy the template to the usable project file
    if [ -e $PROJECT_TEMPLATE_PATH ]; then
        cp $PROJECT_TEMPLATE_PATH $PROJECT_OUT_PATH
    else
        echo "Project template file $PROJECT_TEMPLATE_PATH not found"
        return -1
    fi

    # Replace the parameters in the template
    sed -i "s@\[BUILD_DIR\]@$PROJECT_HOME/build@g" $PROJECT_OUT_PATH
    sed -i "s@\[SRC_DIR\]@$PROJECT_HOME/src@g" $PROJECT_OUT_PATH
    sed -i "s@\[INCL_DIR\]@$PROJECT_HOME/include@g" $PROJECT_OUT_PATH
    sed -i "s@\[TESTS_DIR\]@$PROJECT_HOME/test@g" $PROJECT_OUT_PATH
    sed -i "s@\[COMPILER\]@$BUILD_CC@g" $PROJECT_OUT_PATH
    sed -i "s@\[LINKER\]@$BUILD_CC@g" $PROJECT_OUT_PATH
    sed -i "s@\[C_FLAGS\]@$BUILD_CFLAGS@g" $PROJECT_OUT_PATH
    sed -i "s@\[LD_FLAGS\]@$BUILD_LDFLAGS@g" $PROJECT_OUT_PATH
    sed -i "s@\[STAGING_INCDIR_NATIVE\]@$STAGING_INCDIR_NATIVE@g" $PROJECT_OUT_PATH
    sed -i "s@\[STAGING_INCDIR\]@$STAGING_INCDIR@g" $PROJECT_OUT_PATH

    return 0
}

# Ceedling additional tasks
addtask generate_project_if_none
do_generate_project_if_none[nostamp] = "1"

ceedling_do_generate_project_if_none() {
    # Ceedling paths
    CEEDLING_PV=$(basename `find $GEM_HOME/gems -maxdepth 1 -type d -name "ceedling*" | tail -n 1` | sed s/ceedling-//g);
    CEEDLING_GEM_PATH="${GEM_HOME}/gems/ceedling-${CEEDLING_PV}"
    CEEDLING_BIN=${CEEDLING_GEM_PATH}/bin/ceedling
    PROJECT_HOME="${S}/ops-tests/unit"

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

    ceedling_update_vendor_path ${PROJECT_HOME} ${CEEDLING_GEM_PATH}
    ceedling_create_project_file ${PROJECT_HOME}
    return $?
}

EXPORT_FUNCTIONS do_generate_project_if_none
