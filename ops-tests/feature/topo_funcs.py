# -*- coding: utf-8 -*-
#
# Copyright (C) 2016 Hewlett Packard Enterprise Development LP
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.


def topology_1switch_2host(ops1, hs1, hs2):
    """
    Setting up one switch and two host topology
    sets ports 1 and 6 up
    """
    assert ops1 is not None
    assert hs1 is not None
    assert hs2 is not None

    p1 = ops1.ports['1']
    p2 = ops1.ports['6']

    # Mark interfaces as enabled
    assert not ops1(
        'set interface {p1} user_config:admin=up'.format(**locals()),
        shell='vsctl'
    )
    assert not ops1(
        'set interface {p2} user_config:admin=up'.format(**locals()),
        shell='vsctl'
    )


def topology_2switch_2host(ops1, ops2, hs1, hs2):
    """
    Setting up two switch and two host topology without lag
    sets ports 1 and 5 of both switch1 and switch2 up
    """
    topology_1switch_2host(ops1, hs1, hs2)

    assert ops2 is not None

    p21 = ops2.ports['1']
    p22 = ops2.ports['6']

    assert not ops2(
        'set interface {p21} user_config:admin=up'.format(**locals()),
        shell='vsctl'
    )
    assert not ops2(
        'set interface {p22} user_config:admin=up'.format(**locals()),
        shell='vsctl'
    )


def topology_2switch_2host_lag(ops1, ops2, hs1, hs2):
    """
    Setting up two switch and two host topology with lag
    sets ports 1, 5 and 6 of both switch1 and switch-2 up
    """
    topology_2switch_2host(ops1, ops2, hs1, hs2)
    p15 = ops1.ports['5']
    p25 = ops2.ports['5']

    assert not ops1(
        'set interface {p15} user_config:admin=up'.format(**locals()),
        shell='vsctl'
    )
    assert not ops2(
        'set interface {p25} user_config:admin=up'.format(**locals()),
        shell='vsctl'
    )


def config_switch_l2(ops, vlan_id):
    """
    1 Configuration of one
    Switch (L2 only) (NOT for LAG)
    Configures Interface 1 and 6 for a switch and
    creates VLAN and sets it for interfaces 1 and 6
    """
    config_switch(ops)
    config_vlan(ops, vlan_id)
    with ops.libs.vtysh.ConfigInterface('6') as ctx:
        ctx.vlan_access(vlan_id)


def config_2switch_l2(ops1, ops2, vlan_id1, vlan_id2):
    """
    2 Configuration of two Switch (L2 only)
    (NOT for LAG)
    Configures Interface 1 and 6 for switch1 and switch2 and
    creates VLAN and sets it for interfaces 1 and 6
    """
    config_switch_l2(ops1, vlan_id1)
    config_switch_l2(ops2, vlan_id2)


def config_switch_lag_l2(
                        ops1, ops2, vlan_id1, vlan_id2,
                        lag_id1, lag_id2
                        ):
    """
    3 Configuration of two Switch (L2 only) (LAG)
    Configures Interface 1 and (5&6 FOR lag) for switch1 and switch2 and
    creates VLAN and sets it for interfaces 1 and LAG
    """
    config_switch(ops1)
    config_switch(ops2)
    config_vlan(ops1, vlan_id1)
    config_vlan(ops2, vlan_id2)
    config_additional_port_for_lag(ops1, ops2)
    config_lag_l2(ops1, vlan_id1, lag_id1)
    config_lag_l2(ops2, vlan_id2, lag_id2)


def config_switch_l3(ops, ip1, ip2):
    """
    4 Configuration of one Switch
    (L3 only) (NOT for LAG)
    Configures Interface 1 and 6 for a switch and
    sets IP address to interfaces 1 and 6
    """
    config_switch(ops)
    switch_interface1_ip_address(ops, ip1)
    with ops.libs.vtysh.ConfigInterface('6') as ctx:
        ctx.routing()
        ctx.ip_address(ip2)


def config_switches_l3(
                    ops1, ops2, ip_ops1_int1, ip_ops2_int2, ip_ops1_int6,
                    ip_ops2_int6, ip_route_ops1, ip_route_ops2
                     ):
    """
    5 Configuration of two Switch
    (L3 only) (NOT for LAG)
    Configures Interface 1 and 6 for a switch and
    sets IP address to interfaces 1 and 6
    """
    config_switch(ops1)
    config_switch(ops2)
    switch_interface1_ip_address(ops1, ip_ops1_int1)
    switch_interface1_ip_address(ops2, ip_ops2_int2)
    with ops1.libs.vtysh.ConfigInterface('6') as ctx:
        ctx.routing()
        ctx.ip_address(ip_ops1_int6)
    with ops2.libs.vtysh.ConfigInterface('6') as ctx:
        ctx.routing()
        ctx.ip_address(ip_ops2_int6)
    ip_route_switch(ops1, ip_route_ops1)
    ip_route_switch(ops2, ip_route_ops2)


def config_switches_l3_lag(
                ops1, ops2, ip_ops1_int1, ip_ops2_int2, ip_ops1_lag,
                ip_ops2_lag, ip_route_ops1, ip_route_ops2, lag_id1,
                lag_id2
                ):
    """
    6 Configuration of two Switch (L3 only) (for LAG)
    Configures Interface 1 (5&6 FOR lag) for switch1 and switch2  and
    sets IP address to interfaces 1 and lag
    """
    config_switch(ops1)
    config_switch(ops2)
    config_additional_port_for_lag(ops1, ops2)
    switch_interface1_ip_address(ops1, ip_ops1_int1)
    switch_interface1_ip_address(ops2, ip_ops2_int2)
    config_lag(ops1, lag_id1)
    config_lag(ops2, lag_id1)
    with ops1.libs.vtysh.ConfigInterfaceLag(lag_id1) as ctx:
        ctx.routing()
        ctx.ip_address(ip_ops1_lag)
    with ops2.libs.vtysh.ConfigInterfaceLag(lag_id2) as ctx:
        ctx.routing()
        ctx.ip_address(ip_ops2_lag)
    ip_route_switch(ops1, ip_route_ops1)
    ip_route_switch(ops2, ip_route_ops2)


def config_hosts_l2(hs1, hs2, ip_hs1, ip_hs2):
    """
    Configuration of host (L2 only)
    """
    hs1.libs.ip.interface('1', up=False)
    hs2.libs.ip.interface('1', up=False)
    hs1.libs.ip.interface('1', addr=ip_hs1, up=True)
    hs2.libs.ip.interface('1', addr=ip_hs2, up=True)


def config_hosts_l3(
                hs1, hs2, ip_hs1, ip_hs2,
                ip_route_hs1, ip_route_hs2
                ):
    """
    Configuration of host (L3 only)
    """
    hs1.libs.ip.interface('1', up=False)
    hs2.libs.ip.interface('1', up=False)
    hs1.libs.ip.interface('1', addr=ip_hs1, up=True)
    hs2.libs.ip.interface('1', addr=ip_hs2, up=True)
    hs1(ip_route_hs1)
    hs2(ip_route_hs2)


def ping_test(host, ip):
    """
    Ping test with L2 config
    """
    ping = host.libs.ping.ping(1, ip)
    print(ping)
    assert ping['transmitted'] == ping['received'] == 1


def start_scapy_on_hosts(hs1, hs2):
    """
    Install and start scapy on hosts
    """
    # Having problems with starting scapy sometimes.  The work around is to
    # try at the most twice
    for host in [hs1, hs2]:
        print("Starting scapy for <%s>" % (host.identifier))
        try:
            host.libs.scapy.start_scapy()
        except Exception as err:
            print("Failed to start scapy for <%s> because <%s>, trying again"
                  % (host.identifier, err))
            host.libs.scapy.start_scapy()


def config_vlan(ops, vlan_id):
    """
    Creates vlan 10 and sets it to interface 1
    """
    with ops.libs.vtysh.ConfigVlan(vlan_id) as ctx:
        ctx.no_shutdown()
    with ops.libs.vtysh.ConfigInterface('1') as ctx:
        ctx.vlan_access(vlan_id)


def config_switch(ops):
    """
    Sets interfaces 1 and 6 of a switch to "no shutdown and no routing"
    """
    with ops.libs.vtysh.ConfigInterface('1') as ctx:
        ctx.no_routing()
        ctx.no_shutdown()

    with ops.libs.vtysh.ConfigInterface('6') as ctx:
        ctx.no_routing()
        ctx.no_shutdown()


def config_additional_port_for_lag(ops1, ops2):
    """
    Sets new interface 5 to "no shutdown and no routing for LAG"
    """
    with ops1.libs.vtysh.ConfigInterface('5') as ctx:
        ctx.no_routing()
        ctx.no_shutdown()

    with ops2.libs.vtysh.ConfigInterface('5') as ctx:
        ctx.no_routing()
        ctx.no_shutdown()


def config_lag(ops, lag_id):
    """
    Creates lag10 and sets it to interfaces 5 and interface 6 of a switch
    """
    with ops.libs.vtysh.ConfigInterfaceLag(lag_id) as ctx:
        ctx.no_shutdown()

    with ops.libs.vtysh.ConfigInterface('5') as ctx:
        ctx.lag(lag_id)
    with ops.libs.vtysh.ConfigInterface('6') as ctx:
        ctx.lag(lag_id)


def config_lag_l2(ops, vlan_id, lag_id):
    """
    Creates Interface lag 10 for a switch and sets VLAN 10 to the lag10
    """
    config_lag(ops, lag_id)
    with ops.libs.vtysh.ConfigInterfaceLag(lag_id) as ctx:
        ctx.no_routing()
        ctx.vlan_access(vlan_id)


def switch_interface1_ip_address(ops, ip):
    """
    Sets IP address for interface1 of switch2
    """
    with ops.libs.vtysh.ConfigInterface('1') as ctx:
        ctx.routing()
        ctx.ip_address(ip)


def ip_route_switch(ops1, ip_route):
    """
    Sets IP route for switch-1
    """
    ops1("configure terminal")
    ops1(ip_route)
    ops1("exit")
