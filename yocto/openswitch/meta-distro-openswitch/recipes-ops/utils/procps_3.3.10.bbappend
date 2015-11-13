do_install_append() {
    echo " kernel.sysrq=1
kernel.core_pattern=|/tmp/ops_cdm.sh %e %p %t
kernel.core_uses_pid=0
kernel.core_pipe_limit=4 " >>   ${D}${sysconfdir}/sysctl.conf
}
