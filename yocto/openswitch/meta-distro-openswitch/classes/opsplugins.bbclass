inherit python-dir

OPSPLUGINSDIR = "opsplugins"
OPSPLUGINSPATH = "${PYTHON_SITEPACKAGES_DIR}/${OPSPLUGINSDIR}"

FILES_${PN} += "${OPSPLUGINSPATH}/*"

copy_ops_plugins() {
    # Find the plugins directory in the source directory
    plugindir=$(find ${S} -name ${OPSPLUGINSDIR})

    if [ "$plugindir" != "" ]
    then
        # Get the staging plugins path to detect if __init__.py was already
        # installed
        staging_plugins_path=${STAGING_DIR_TARGET}/${OPSPLUGINSPATH}

        # Target path is used for installation of the files
        target_plugins_path=${D}/${OPSPLUGINSPATH}

        # Create the plugins path for installation
        install -d ${target_plugins_path}

        # Find all python files in the plugins dir
        plugins=$(find ${plugindir} -name "*.py")

        # Make the plugins dir a python package by installing __init__.py
        # if it doesn't already exist in the staging's python ops plugin dir.
        # This file should only be installed once.
        if [ ! -e "${staging_plugins_path}/__init__.py" ]
        then
            touch ${target_plugins_path}/__init__.py
        fi

        for plugin in $plugins; do \
            cp ${plugin} ${target_plugins_path}
        done
    fi
}
