#
# Parse .ops-config file and add new IMAGE_FEATURES based on what's been
# enabled by user via Kconfig mechanism.
#
python __anonymous () {
    config_file_path = d.getVar("TOPDIR") + "/.ops-config"
    config_file = open(config_file_path, "r")

    for line in config_file:
        if line.startswith("OPS_CONFIG"):
            config_param = line.split('=', 1)[0]
            ops_feature = " " + config_param.split('_', 2)[2]
            d.appendVar("IMAGE_FEATURES", ops_feature)

    config_file.close()
}
