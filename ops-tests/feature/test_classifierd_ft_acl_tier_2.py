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

"""
OpenSwitch Test for ACL operations with UDP traffic.
This file consists of the following test cases:

Test1 : acl_udp_any_any_permit
Test2 : acl_udp_any_any_deny
Test3 : acl_permit_udp_hs1_hs2
Test4 : acl_deny_udp_hs1_hs2
Test5 : acl_permit_udp_prefix_len_mask
Test6 : acl_deny_udp_prefix_len_mask
Test7 : acl_permit_udp_dotted_netmask
Test8 : acl_deny_udp_dotted_netmask
Test9 : acl_permit_udp_non_contiguous_mask
Test10: acl_deny_udp_non_contiguous_mask
Test11: acl_permit_udp_dport_eq_param
Test12: acl_deny_udp_dport_eq_param
Test13: acl_deny_udp_dport_eq_param
Test14: acl_deny_udp_dport_eq_param
Test15: acl_modify_after_sending_udp_traffic
Test16: acl_deny_udp_on_multiple_ports
Test17: acl_permit_icmp_on_multiple_ports
Test18: acl_replace_with_icmp_traffic
Test19: acl_permit_any_hs1_hs2_hitcount
Test20: test_acl_permit_any_hs1_hs2_config_persistence_ten_entries
Test21: test_acl_permit_any_hs1_hs2_config_persistence_300_entries
Test22: test_acl_permit_any_hs1_hs2_config_persistence_150x2_entries
"""

from time import sleep
from pytest import mark
from pytest import fixture

from topo_defs import topology_1switch_2host_def
from acl_common_test_suite import CommonTestSuite
from topo_funcs import topology_1switch_2host
from topo_funcs import config_switch_l2
from topo_funcs import config_hosts_l2
from topo_funcs import ping_test
from topo_funcs import start_scapy_on_hosts
from acl_classifier_common_lib import wait_until_interface_up

TOPOLOGY = topology_1switch_2host_def
ingress_acl_test = CommonTestSuite()


@fixture(scope='module')
def configure_acl_test(topology):
    ops1 = topology.get('ops1')
    hs1 = topology.get('hs1')
    hs2 = topology.get('hs2')

    topology_1switch_2host(ops1, hs1, hs2)

    ingress_acl_test.set_acl_addr_type('ip')
    ingress_acl_test.set_acl_app_type('port')
    ingress_acl_test.set_acl_direction('in')
    ingress_acl_test.set_topology(topology)
    ingress_acl_test.set_switch_1(ops1)
    ingress_acl_test.set_tx_host(hs1, '1.1.1.1')
    ingress_acl_test.set_rx_host(hs2, '1.1.1.2')
    ingress_acl_test.set_primary_interface_number('1')
    ingress_acl_test.set_secondary_interface_number('6')
    ingress_acl_test.set_tx_count(10)

    config_switch_l2(ops1, '100')

    hs1.send_command('service network-manager stop', shell='bash')
    hs2.send_command('service network-manager stop', shell='bash')

    config_hosts_l2(hs1, hs2, ip_hs1='1.1.1.1/24', ip_hs2='1.1.1.2/24')

    # Delaying to allow commands to be implemented
    sleep(5)
    for portlbl in ['1', '6']:
        wait_until_interface_up(ops1, portlbl)

    ping_test(hs2, '1.1.1.1')
    start_scapy_on_hosts(hs1, hs2)


@mark.platform_incompatible(['docker'])
def test_acl_permit_udp_any_any(configure_acl_test, topology, step):
    """
    This test adds a "1 permit udp any any" rule on interface 1.
    It then sends 10 UDP packets from hs1 to hs2 and verifies that
    10 UDP packets are received on hs2
    """
    ingress_acl_test.acl_action_udp_any_any(step, 1, 'permit')


@mark.platform_incompatible(['docker'])
def test_acl_deny_udp_any_any(configure_acl_test, topology, step):
    """
    This test adds a "1 deny udp any any" rule on interface 1.
    It then sends 10 UDP packets from hs1 to hs2 and verifies that
    10 UDP packets are NOT received on hs2
    """
    ingress_acl_test.acl_action_udp_any_any(step, 2, 'deny')


@mark.platform_incompatible(['docker'])
def test_acl_permit_udp_hs1_hs2(configure_acl_test, topology, step):
    """
    This test adds a "1 permit udp 1.1.1.1 1.1.1.2" rule on interface 1.
    It then sends 10 UDP packets from hs1 to hs2 and verifies that
    10 UDP packets are received on hs2. Also, it verifies that other
    protocol traffic is NOT received by hs2 by sending 10 ICMP packets.
    """
    ingress_acl_test.acl_action_udp_hs1_hs2(step, 3, 'permit')


@mark.platform_incompatible(['docker'])
def test_acl_deny_udp_hs1_hs2(configure_acl_test, topology, step):
    """
    This test adds a "1 deny udp 1.1.1.1 1.1.1.2" rule on interface 1.
    It then passes 10 UDP packets from hs1 to hs2 and verifies that
    10 UDP packets are denied on hs2. Also, it verifies that other
    protocol traffic is NOT received by hs2 by sending 10 ICMP packets.
    """
    ingress_acl_test.acl_action_udp_hs1_hs2(step, 4, 'deny')


@mark.platform_incompatible(['docker'])
def test_acl_permit_udp_prefix_len_mask(configure_acl_test, topology, step):
    """
    This test adds a "1 permit udp 1.1.1.0/31 1.1.1.0/30" rule on interface 1.
    It then passes 10 UDP packets from hs1 to hs2 and verifies that
    10 UDP packets are received on hs2. Also, it verifies that other
    protocol traffic is NOT received by hs2 by sending 10 ICMP packets.
    """
    ingress_acl_test.acl_action_udp_prefix_len_mask(step, 5, 'permit')


@mark.platform_incompatible(['docker'])
def test_acl_deny_udp_prefix_len_mask(configure_acl_test, topology, step):
    """
    This test adds a "1 deny udp 1.1.1.0/31 1.1.1.0/30" rule on interface 1.
    It then passes 10 UDP packets from hs1 to hs2 and verifies that
    10 UDP packets are received on hs2. Also, it verifies that other
    protocol traffic is NOT received by hs2 by sending 10 ICMP packets.
    """
    ingress_acl_test.acl_action_udp_prefix_len_mask(step, 6, 'deny')


@mark.platform_incompatible(['docker'])
def test_acl_permit_udp_dotted_netmask(configure_acl_test, topology, step):
    """
    This test adds a "1 permit udp 1.1.1.0/255.255.255.254
    1.1.1.0/255.255.255.0" rule on interface 1.
    It then passes 10 UDP packets from hs1 to hs2 and verifies that
    10 UDP packets are received on hs2. Also, it verifies that other
    protocol traffic is NOT received by hs2 by sending 10 ICMP packets.
    """
    ingress_acl_test.acl_action_udp_dotted_netmask(step, 7, 'permit')


@mark.platform_incompatible(['docker'])
def test_acl_deny_udp_dotted_netmask(configure_acl_test, topology, step):
    """
    This test adds a "1 deny udp 1.1.1.0/255.255.255.254
    1.1.1.0/255.255.255.0" rule on interface 1.
    It then passes 10 UDP packets from hs1 to hs2 and verifies that
    10 UDP packets are NOT received on hs2. Also, it verifies that other
    protocol traffic is NOT received by hs2 by sending 10 ICMP packets.
    """
    ingress_acl_test.acl_action_udp_dotted_netmask(step, 8, 'deny')


@mark.platform_incompatible(['docker'])
def test_acl_permit_udp_non_contiguous_mask(
                                      configure_acl_test, topology, step):
    """
    This test adds a "1 permit udp 1.0.1.0/255.0.255.254
    any" rule on interface 1. It then passes 10 UDP packets from hs1 to hs2
    and verifies that 10 UDP packets are received on hs2. Also, it verifies
    that other protocol traffic is NOT received by hs2 by sending 10 ICMP
    packets.
    """
    ingress_acl_test.acl_action_udp_non_contiguous_mask(step, 9, 'permit')


@mark.platform_incompatible(['docker'])
def test_acl_deny_udp_non_contiguous_mask(configure_acl_test, topology,
                                          step):
    """
    This test adds a "1 deny udp 1.0.1.0/255.0.255.254
    any" rule on interface 1. It then passes 10 UDP packets from hs1 to hs2
    and verifies that 10 UDP packets are NOT received on hs2. Also, it verifies
    that other protocol traffic is NOT received by hs2 by sending 10 ICMP
    packets.
    """
    ingress_acl_test.acl_action_udp_non_contiguous_mask(step, 10, 'deny')


@mark.platform_incompatible(['docker'])
def test_acl_permit_udp_dport_eq_param(configure_acl_test, topology, step):
    """
    This test adds a "1 permit udp 1.1.1.1 1.1.1.2 eq 48621" rule on
    interface 1.
    It then passes 10 UDP packets from hs1 to hs2 and verifies that
    10 UDP packets are received on hs2. Also, it verifies that other
    UDP traffic on another destination port is NOT received by hs2.
    """
    ingress_acl_test.acl_action_udp_dport_eq_param(step, 11, 'permit')


@mark.platform_incompatible(['docker'])
def test_acl_deny_udp_dport_eq_param(configure_acl_test, topology, step):
    """
    This test adds a "1 deny udp 1.1.1.1 1.1.1.2 eq 48621" rule on
    interface 1.
    It then passes 10 UDP packets from hs1 to hs2 and verifies that
    10 UDP packets are received on hs2. Also, it verifies that other
    UDP traffic on another destination port is NOT received by hs2.
    """
    ingress_acl_test.acl_action_udp_dport_eq_param(step, 12, 'deny')


@mark.platform_incompatible(['docker'])
def test_acl_permit_udp_sport_eq_param(configure_acl_test, topology, step):
    """
    This test adds a "1 permit udp 1.1.1.1 eq 5555 1.1.1.2" rule on
    interface 1.
    It then passes 10 UDP packets from hs1 to hs2 and verifies that
    10 UDP packets are received on hs2. Also, it verifies that other
    UDP traffic on another destination port is NOT received by hs2.
    """
    ingress_acl_test.acl_action_udp_sport_eq_param(step, 13, 'permit')


@mark.platform_incompatible(['docker'])
def test_acl_deny_udp_sport_eq_param(configure_acl_test, topology, step):
    """
    This test adds a "1 deny udp 1.1.1.1 eq 5555 1.1.1.2" rule on
    interface 1.
    It then passes 10 UDP packets from hs1 to hs2 and verifies that
    10 UDP packets are NOT received on hs2. Also, it verifies that other
    UDP traffic on another destination port is NOT received by hs2.
    """
    ingress_acl_test.acl_action_udp_sport_eq_param(step, 14, 'deny')


@mark.platform_incompatible(['docker'])
def test_acl_modify_after_sending_udp_traffic(
                                        configure_acl_test, topology, step):
    """
    This test sends some traffic after applying an ACL to interface 1.
    It then stops traffic, modifies the ACL and verifies that traffic behavior
    complies with the applied ACL
    """
    ingress_acl_test.acl_modify_after_sending_udp_traffic(step, 15)


@mark.platform_incompatible(['docker'])
def test_acl_deny_udp_on_multiple_ports(configure_acl_test, topology, step):
    """
    This tests applies a deny rule for UDP and permit rule for ICMP on
    interfaces 1 and 2. Then, it passes UDP traffic in both directions
    and verifies that traffic is blocked. Next, it passes ICMP traffic
    and verifies that the responses are received.
    """
    ingress_acl_test.acl_deny_udp_on_multiple_ports(step, 16)


@mark.platform_incompatible(['docker'])
def test_acl_permit_icmp_on_multiple_ports(configure_acl_test, topology,
                                           step):
    """
    This test sends ICMP traffic from hs1 to hs2 after applying a permit
    ACL to interface 1. After it verifies that hs2 receives 10 packets,
    it sends traffic in the reverse direction and verifies that traffic
    behavior complies with the applied permit ACL
    """
    ingress_acl_test.acl_permit_icmp_on_multiple_ports(step, 17)


@mark.platform_incompatible(['docker'])
def test_acl_replace_with_icmp_traffic(configure_acl_test, topology, step):
    """
    This test sends 10 ICMP packets from hs1 to hs2 with an ACL applied
    on interface 1. Verifies that the packets have been received on hs2.
    It then replaces this ACL with a deny ACL and verifies that no traffic
    is seen on hs2.
    """
    ingress_acl_test.acl_replace_with_icmp_traffic(step, 18)


@mark.platform_incompatible(['docker'])
def test_acl_permit_icmp_hitcount(configure_acl_test, topology, step):
    """
    This test adds a "50 permit any 1.1.1.1 1.1.1.2 count" rule on
    interface 1. It then sends 10 ICMP packets from hs1 to hs2 and verifies
    that 10 ICMP packets are received on hs2 and the hitcount equals 10
    """
    ingress_acl_test.acl_permit_icmp_hitcount(step, 19)


@mark.platform_incompatible(['docker'])
def test_acl_permit_any_hs1_hs2_config_persistence_ten_entries(
                                    configure_acl_test, topology, step
                                    ):
    """
    This test adds a sequence of 10 " permit any 1.1.1.1 1.1.1.2 count"
    rules on interface 1. It then sends 10 ICMP packets from hs1 to hs2
    and verifies that configuration is persisted
    """
    ingress_acl_test.acl_config_persistance(step, 20, entries_list=[10])


@mark.platform_incompatible(['docker'])
def test_acl_permit_any_hs1_hs2_config_persistence_300_entries(
                                    configure_acl_test, topology, step
                                    ):
    """
    This test adds a sequence of 300
    rules on interface 1. It then sends 10 ICMP packets from hs1 to hs2
    and verifies that configuration is persisted
    """
    ingress_acl_test.acl_config_persistance(step, 21, entries_list=[300])


@mark.platform_incompatible(['docker'])
def test_acl_permit_any_hs1_hs2_config_persistence_150x2_entries(
                                    configure_acl_test, topology, step
                                    ):
    """
    This test adds a sequence of 150 rules each on interface 1 and 2. It
    then sends 10 ICMP packets from hs1 to hs2 and verifies that
    configuration is persisted
    """
    ingress_acl_test.acl_config_persistance(step, 22, entries_list=[150, 150])
