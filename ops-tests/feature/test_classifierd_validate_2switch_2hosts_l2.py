# -*- coding: utf-8 -*-
#
# Copyright (C) 2015-2016 Hewlett Packard Enterprise Development LP
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

"""
OpenSwitch Test for simple ping between nodes.
"""

from topo_defs import topology_2switch_2host_def
from topo_funcs import topology_2switch_2host
from topo_funcs import config_2switch_l2
from topo_funcs import config_hosts_l2
from topo_funcs import ping_test
from acl_classifier_common_lib import wait_until_interface_up

ip_hs1 = '10.10.10.1'
ip_hs2 = '10.10.10.2'
ip_hs1_bitlength = '10.10.10.1/24'
ip_hs2_bitlength = '10.10.10.2/24'
vlan_id_s1 = 10
vlan_id_s2 = 10

TOPOLOGY = topology_2switch_2host_def


def test_validate_2switch_2host_l2(topology):

    ops1 = topology.get('ops1')
    ops2 = topology.get('ops2')
    hs1 = topology.get('hs1')
    hs2 = topology.get('hs2')
    topology_2switch_2host(ops1, ops2, hs1, hs2)
    config_2switch_l2(ops1, ops2, vlan_id_s1, vlan_id_s2)
    config_hosts_l2(hs1, hs2, ip_hs1_bitlength, ip_hs2_bitlength)
    for portlbl in ['1', '6']:
        wait_until_interface_up(ops1, portlbl)
    for portlbl in ['1', '6']:
        wait_until_interface_up(ops2, portlbl)
    ping_test(hs1, ip_hs2)
    ops1('show run')
    ops2('show run')
