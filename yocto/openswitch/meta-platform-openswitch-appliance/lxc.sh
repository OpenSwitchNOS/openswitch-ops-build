copy_machine_configuration()
{
    path=$1
    rootfs=$2
    name=$3
    link=$4

    cat <<EOF >> $path/config

# Dataplane Interfaces

lxc.network.type = macvlan
lxc.network.macvlan.mode = private
lxc.network.flags = up
lxc.network.link = $link
lxc.network.name = eth1
lxc.network.mtu = 1500

lxc.network.type = macvlan
lxc.network.macvlan.mode = private
lxc.network.flags = up
lxc.network.link = $link
lxc.network.name = eth2
lxc.network.mtu = 1500

lxc.network.type = macvlan
lxc.network.macvlan.mode = private
lxc.network.flags = up
lxc.network.link = $link
lxc.network.name = eth3
lxc.network.mtu = 1500
EOF
}
