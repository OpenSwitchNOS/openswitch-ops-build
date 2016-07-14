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

import topology_lib_vtysh
import re
from ipaddress import ip_address, IPv4Address, IPv6Address
from time import sleep
from random import choice, randrange

from acl_classifier_common_lib import configure_acl_l3
from acl_classifier_common_lib import apply_acl
from acl_classifier_common_lib import unconfigure_acl
from acl_classifier_common_lib import create_and_verify_traffic
from acl_classifier_common_lib import reboot_switch


class CommonTestSuite:

    def __init__(self):
        self._aclAddrType = None
        self._aclApp = None
        self._aclDir = None
        self._topo = None
        self._sw1 = None
        self._tx_host = None
        self._rx_host = None
        self._pri_int = None
        self._sec_int = None
        self._tx_count = None

    def set_acl_addr_type(self, addr_type):
        # In the future, the address type will include more address types
        #     valid_args = ('mac', 'ip', 'ipv6')
        valid_args = ('ip')
        assert addr_type in valid_args
        self._aclAddrType = addr_type

    def set_acl_app_type(self, app):
        # In the future, we will be able to specifiy more applications of ACLs
        #     valid_args = ('port', 'vlan', 'tunnel')
        valid_args = ('port')
        assert app in valid_args
        self._aclApp = app

    def set_acl_direction(self, direction):
        valid_args = ('in', 'out')
        assert direction in valid_args
        self._aclDir = direction

    def set_topology(self, topology):
        assert topology is not None
        self._topo = topology

    def set_switch_1(self, switch):
        assert switch is not None
        self._sw1 = switch

    def set_tx_host(self, host, host_ip):
        assert host is not None
        assert isinstance(host_ip, str)
        self._tx_host = host
        self._tx_host_ip = host_ip

    def set_rx_host(self, host, host_ip):
        assert host is not None
        assert isinstance(host_ip, str)
        self._rx_host = host
        self._rx_host_ip = host_ip

    def set_primary_interface_number(self, inter_str):
        # If the acl application type is port, then the primary interface
        # number is the port number the ACLs will be primarily applied to.
        # For VLANs, the vlan number.  For tunnels, the tunnel number.
        assert isinstance(inter_str, str)
        self._pri_int = inter_str

    def set_secondary_interface_number(self, inter_str):
        assert isinstance(inter_str, str)
        self._sec_int = inter_str

    def set_tx_count(self, count):
        assert isinstance(count, int)
        self._tx_count = count

    def _create_and_verify_traffic_l3(self, proto, rx_expect,
                                      direction='forward',
                                      src_port='5555', dst_port='48621'):
        # Sends udp traffic from tx_host to rx_host, or in reverse if the
        # direction is specified as false.  No need to specify layer 4
        # port numbers when using ICMP (they will be deleted if you do).
        assert proto in ('UDP', 'TCP', 'ICMP')
        assert isinstance(rx_expect, bool)
        assert direction in ("forward", "reverse")
        assert isinstance(src_port, str)
        assert isinstance(dst_port, str)

        if direction == "forward":
            tx_host, src_ip = self._tx_host, self._tx_host_ip
            rx_host, dst_ip = self._rx_host, self._rx_host_ip
        else:
            tx_host, src_ip = self._rx_host, self._rx_host_ip
            rx_host, dst_ip = self._tx_host, self._tx_host_ip

        if self._aclAddrType == 'ip':
            proto_str = 'IP/' + proto
            if proto == 'UDP' or proto == 'TCP':
                filter_str = "lambda p: %s in p and p[UDP].dport == %s and " \
                             "p[IP].src == '%s' and p[IP].dst == '%s'" \
                             % (proto, dst_port, src_ip, dst_ip)
            elif proto == 'ICMP':
                filter_str = "lambda p: ICMP in p and p[IP].src == '%s' " \
                             "and p[IP].dst == '%s'" \
                             % (src_ip, dst_ip)
                src_port = dst_port = ''
            else:
                # Unrecognized L4 protocol
                assert False

            create_and_verify_traffic(
                        self._topo, tx_host, rx_host, src_ip,
                        src_port, dst_ip, dst_port, proto_str,
                        filter_str, self._tx_count, rx_expect
                        )
        else:
            # ipv6 and mac not implemented yet
            assert False

    def _check_basic_private_variables(self, step_num):
        # Checks the most basic private variables that ALL tests must have set
        # prior to the tests running.  Do not add tests for variables that not
        # all tests need
        assert isinstance(self._aclAddrType, str)
        assert isinstance(self._aclApp, str)
        assert isinstance(self._aclDir, str)
        assert self._topo is not None
        assert self._sw1 is not None
        assert self._tx_host is not None
        assert self._rx_host is not None
        assert isinstance(self._pri_int, str)
        assert isinstance(self._sec_int, str)
        assert isinstance(self._tx_count, int)
        assert isinstance(step_num, int)

    def print_clr(self, why, what):
        assert why in ('FAIL', 'WARNING', 'PASS', 'INFO')
        assert isinstance(what, str)
        color_dict = {'FAIL': '\033[41m\033[30m',
                      'WARNING': '\033[103m\033[30m',
                      'PASS': '\033[42m\033[30m',
                      'INFO': '\033[44m\033[37m',
                      'END': '\033[0m'}
        print(color_dict[why] + what + color_dict['END'])

    def _rm_host_num_and_add_prefix(self, ip_str, prefix_str):
        # Strips the host number off of the ip address based on the prefix.
        # returns the prefix appended to the network id
        # examples:
        #     add_prefix('1.1.1.1', '255.255.255.252')
        #         return '1.1.1.0/255.255.255.252'
        #     add_prefix('1.1.1.1', '16')
        #         return '1.1.0.0/16'
        #     add_prefix('1.1.1.1', '16')
        #         return '1.1.0.0/16'
        #     add_prefix('2001:db8:3c4d:15:abcd:1234:5678:9abc', '65')
        #        return '2001:db8:3c4d:15:8000::/65'
        #     add_prefix('2001:db8:3c4d:15:abcd:1234:5678:9abc', 'FFFF:FFFF::')
        #        return '2001:db8::/FFFF:FFFF::'
        assert isinstance(ip_str, str)
        assert isinstance(prefix_str, str)
        try:
            prefix_val = int(ip_address(prefix_str))
        except ValueError:
            if ip_address(ip_str).version == 4:
                prefix_val = (0xFFFFFFFF << (32 - int(prefix_str)))
            else:
                prefix_val = ((2**128 - 1) << (128 - int(prefix_str)))
        ip_val = int(ip_address(ip_str)) & prefix_val
        if ip_address(ip_str).version == 4:
            ip_str = str(IPv4Address(ip_val))
        else:
            ip_str = str(IPv6Address(ip_val))
        return (ip_str + '/' + prefix_str)

    def _rand_ip_address_with_prefix(self):
        if self._aclAddrType == 'ip':
            rand_ip_addr = str(IPv4Address(randrange(0, 0xFFFFFFFF)))
            rand_prefix = choice([str(IPv4Address(randrange(0, 0xFFFFFFFF))),
                                  str(randrange(0, 32))])
            return choice([
                rand_ip_addr,
                self._rm_host_num_and_add_prefix(rand_ip_addr, rand_prefix),
                'any'
                ])

    def _rand_port(self, proto_str, range_lt_gt_count=0):
        assert isinstance(proto_str, str)
        assert isinstance(range_lt_gt_count, int)
        retval = ''

        # The only protocols that can use port numbers are sctp, tcp, and udp.
        if self._aclAddrType == 'ip' and re.match("sctp|tcp|udp", proto_str):
            # The AS5712 can only have at most 32 range, lt, or gt operators
            # total.
            if range_lt_gt_count < 32:
                # 'neq' is not supported in the AS5712
                rand_port_op = choice(['eq ', 'gt ', 'lt '])
                rand_port_num = randrange(0, 65535)
                rand_port_range = "range " + str(rand_port_num) + " " + \
                    str(randrange(rand_port_num, 65535))
                retval = choice([rand_port_op + str(rand_port_num),
                                 rand_port_range,
                                 ''])
                if re.match("range|gt|lt", retval):
                    return retval, range_lt_gt_count + 1
            else:
                retval = choice(['eq ' + str(randrange(0, 65535)), ''])
        return retval, range_lt_gt_count

    def _get_hitcount(self, sw, acl_name, inter_num, target_ace_str):
        assert sw is not None
        assert isinstance(acl_name, str)
        assert isinstance(inter_num, str)
        assert isinstance(target_ace_str, str)
        self.print_clr("INFO", "_get_hitcount(sw, %s, %s, %s)"
                       % (acl_name, inter_num, target_ace_str))

        hit_dict = {}
        if self._aclApp == 'port':
            hit_dict = sw.libs.vtysh.show_access_list_hitcounts_ip_interface(
                                 acl_name, self._sw1.ports[inter_num])
            if hit_dict is None or hit_dict == {}:
                self.print_clr("FAIL", "show access-list hitcounts ip %s"
                               " interface %s returned nothing"
                               % (acl_name, self._sw1.ports[inter_num]))
                return 'NoResponse'
            rule_re = re.compile(re.sub('\.', r'\.',
                                 re.sub('\s+', '\s+', target_ace_str.strip())))
            print("rule_re=<%s>" % (rule_re))
            for rule, count in hit_dict.items():
                print("rule=<%s>: count<%s>" % (rule, count))
                if re.search(rule_re, rule):
                    self.print_clr("PASS", "Found the right rule <%s>"
                                   % (rule))
                    return count
            self.print_clr("FAIL", "Did not find the hitcounts for <%s>"
                           % (target_ace_str))
            return 'TargetAceNotFound'
        else:
            self.print_clr("FAIL", "Unimplemented ACL Application type")
            assert False

    def _clear_hitcounts(self, acl_name, interface_num):
        assert isinstance(acl_name, str)
        assert isinstance(interface_num, str)

        if self._aclApp == 'port':
            if self._aclAddrType == 'ip':
                self._sw1.libs.vtysh.clear_access_list_hitcounts_ip_interface(
                    acl_name, self._sw1.ports[interface_num]
                    )
            else:
                self.print_clr("FAIL",
                               "The ACL address type <%s> is not implemented"
                               " yet" % (self._aclAddrType))
                assert False
        else:
            self.print_clr("FAIL",
                           "The ACL application type <%s> is not implemented"
                           " yet" % (self._aclApp))
            assert False

    def acl_action_udp_any_any(self, step, step_num, action_str):
        self._check_basic_private_variables(step_num)

        assert isinstance(action_str, str) and \
            action_str in ('permit', 'deny')

        acl_name = 'test'

        src_ip = 'any'
        dst_ip = 'any'
        step(str(step_num) +
             '.a Configure an ACL with 1 %s udp %s %s rule'
             % (action_str, src_ip, dst_ip))

        seq_num = '1'
        action = action_str
        proto = 'udp'
        src_port = ''
        dst_port = ''
        count_str = ''
        configure_acl_l3(
            self._sw1, self._aclAddrType, acl_name, seq_num, action, proto,
            src_ip, src_port, dst_ip, dst_port, count_str
        )

        apply_acl(sw=self._sw1,
                  app_type=self._aclApp,
                  interface_num=self._pri_int,
                  acl_addr_type=self._aclAddrType,
                  acl_name=acl_name,
                  direction=self._aclDir)

        step(str(step_num) + '.b Create and verify UDP packets')
        rx_expect = True if action_str == 'permit' else False
        self._create_and_verify_traffic_l3('UDP', rx_expect)

        step(str(step_num) + '.c Remove the configured ACL')
        unconfigure_acl(self._sw1, self._aclAddrType, acl_name)

    def acl_action_udp_hs1_hs2(self, step, step_num, action_str):
        self._check_basic_private_variables(step_num)

        assert isinstance(action_str, str) and \
            action_str in ('permit', 'deny')

        acl_name = 'test'

        src_ip = self._tx_host_ip
        dst_ip = self._rx_host_ip
        step(str(step_num) +
             '.a Configure an ACL with 1 %s udp %s %s rule'
             % (action_str, src_ip, src_ip))

        seq_num = '1'
        action = action_str
        proto = 'udp'
        src_port = ''
        dst_port = ''
        count_str = ''
        configure_acl_l3(
            self._sw1, self._aclAddrType, acl_name, seq_num, action, proto,
            src_ip, src_port, dst_ip, dst_port, count_str
        )

        apply_acl(sw=self._sw1,
                  app_type=self._aclApp,
                  interface_num=self._pri_int,
                  acl_addr_type=self._aclAddrType,
                  acl_name=acl_name,
                  direction=self._aclDir)

        step(str(step_num) + '.b Create and verify UDP packets')
        rx_expect = True if action_str == 'permit' else False
        self._create_and_verify_traffic_l3('UDP', rx_expect)

        step(str(step_num) + '.c Create and verify ICMP packets')
        rx_expect = False
        self._create_and_verify_traffic_l3('ICMP', rx_expect)

        step(str(step_num) + '.d Remove the configured ACL')
        unconfigure_acl(self._sw1, self._aclAddrType, acl_name)

    def acl_action_udp_prefix_len_mask(self, step, step_num, action_str):
        self._check_basic_private_variables(step_num)

        assert isinstance(action_str, str) and \
            action_str in ('permit', 'deny')

        acl_name = 'test'

        src_ip = self._rm_host_num_and_add_prefix(self._tx_host_ip, '31')
        dst_ip = self._rm_host_num_and_add_prefix(self._rx_host_ip, '30')
        step(str(step_num) +
             '.a Configure an ACL with 1 %s udp %s %s rule'
             % (action_str, src_ip, dst_ip))

        seq_num = '1'
        action = action_str
        proto = 'udp'
        src_port = ''
        dst_port = ''
        count_str = ''
        configure_acl_l3(
            self._sw1, self._aclAddrType, acl_name, seq_num, action, proto,
            src_ip, src_port, dst_ip, dst_port, count_str
        )

        apply_acl(sw=self._sw1,
                  app_type=self._aclApp,
                  interface_num=self._pri_int,
                  acl_addr_type=self._aclAddrType,
                  acl_name=acl_name,
                  direction=self._aclDir)

        step(str(step_num) + '.b Create and verify UDP packets')
        rx_expect = True if action_str == 'permit' else False
        self._create_and_verify_traffic_l3('UDP', rx_expect)

        step(str(step_num) + '.c Create and verify ICMP packets')
        rx_expect = False
        self._create_and_verify_traffic_l3('ICMP', rx_expect)

        step(str(step_num) + '.d Remove the configured ACL')
        unconfigure_acl(self._sw1, self._aclAddrType, acl_name)

    def acl_action_udp_dotted_netmask(self, step, step_num, action_str):
        self._check_basic_private_variables(step_num)

        assert isinstance(action_str, str) and \
            action_str in ('permit', 'deny')

        acl_name = 'test'

        src_ip = self._rm_host_num_and_add_prefix(self._tx_host_ip,
                                                  '255.255.255.254')
        dst_ip = self._rm_host_num_and_add_prefix(self._rx_host_ip,
                                                  '255.255.255.0')
        step(str(step_num) +
             '.a Configure an ACL with 1 %s udp %s %s rule'
             % (action_str, src_ip, dst_ip))

        seq_num = '1'
        action = action_str
        proto = 'udp'
        src_port = ''
        dst_port = ''
        count_str = ''
        configure_acl_l3(
            self._sw1, self._aclAddrType, acl_name, seq_num, action, proto,
            src_ip, src_port, dst_ip, dst_port, count_str
        )

        apply_acl(sw=self._sw1,
                  app_type=self._aclApp,
                  interface_num=self._pri_int,
                  acl_addr_type=self._aclAddrType,
                  acl_name=acl_name,
                  direction=self._aclDir)

        step(str(step_num) + '.b Create and verify UDP packets')
        rx_expect = True if action_str == 'permit' else False
        self._create_and_verify_traffic_l3('UDP', rx_expect)

        step(str(step_num) + '.c Create and verify ICMP packets')
        rx_expect = False
        self._create_and_verify_traffic_l3('ICMP', rx_expect)

        step(str(step_num) + '.d Remove the configured ACL')
        unconfigure_acl(self._sw1, self._aclAddrType, acl_name)

    def acl_action_udp_non_contiguous_mask(self, step, step_num, action_str):
        self._check_basic_private_variables(step_num)

        assert isinstance(action_str, str) and \
            action_str in ('permit', 'deny')

        acl_name = 'test'

        src_ip = self._rm_host_num_and_add_prefix(self._tx_host_ip,
                                                  '255.0.255.254')
        dst_ip = 'any'
        step(str(step_num) +
             '.a Configure an ACL with 1 %s udp %s %s rule'
             % (action_str, src_ip, dst_ip))

        seq_num = '1'
        action = action_str
        proto = 'udp'
        src_port = ''
        dst_port = ''
        count_str = ''
        configure_acl_l3(
            self._sw1, self._aclAddrType, acl_name, seq_num, action, proto,
            src_ip, src_port, dst_ip, dst_port, count_str
        )

        apply_acl(sw=self._sw1,
                  app_type=self._aclApp,
                  interface_num=self._pri_int,
                  acl_addr_type=self._aclAddrType,
                  acl_name=acl_name,
                  direction=self._aclDir)

        step(str(step_num) + '.b Create and verify UDP packets')
        rx_expect = True if action_str == 'permit' else False
        self._create_and_verify_traffic_l3('UDP', rx_expect)

        step(str(step_num) + '.c Create and verify ICMP packets')
        rx_expect = False
        self._create_and_verify_traffic_l3('ICMP', rx_expect)

        step(str(step_num) + '.d Remove the configured ACL')
        unconfigure_acl(self._sw1, self._aclAddrType, acl_name)

    def acl_action_udp_dport_eq_param(self, step, step_num, action_str):
        self._check_basic_private_variables(step_num)

        assert isinstance(action_str, str) and \
            action_str in ('permit', 'deny')

        acl_name = 'test'

        src_ip = self._tx_host_ip
        dst_ip = self._rx_host_ip
        dst_port = 'eq 48621'
        step(str(step_num) +
             '.a Configure an ACL with 1 %s udp %s %s %s rule'
             % (action_str, src_ip, dst_ip, dst_port))

        seq_num = '1'
        action = action_str
        proto = 'udp'
        src_port = ''
        count_str = ''
        configure_acl_l3(
            self._sw1, self._aclAddrType, acl_name, seq_num, action, proto,
            src_ip, src_port, dst_ip, dst_port, count_str
        )

        apply_acl(sw=self._sw1,
                  app_type=self._aclApp,
                  interface_num=self._pri_int,
                  acl_addr_type=self._aclAddrType,
                  acl_name=acl_name,
                  direction=self._aclDir)

        step(str(step_num) + '.b Create and verify UDP packets')
        rx_expect = True if action_str == 'permit' else False
        self._create_and_verify_traffic_l3('UDP', rx_expect)

        step(str(step_num) + '.c Create and verify other UDP packets')
        rx_expect = False
        self._create_and_verify_traffic_l3('UDP', rx_expect, src_port='1000',
                                           dst_port='5555')

        step(str(step_num) + '.d Remove the configured ACL')
        unconfigure_acl(self._sw1, self._aclAddrType, acl_name)

    def acl_action_udp_sport_eq_param(self, step, step_num, action_str):
        self._check_basic_private_variables(step_num)

        assert isinstance(action_str, str) and \
            action_str in ('permit', 'deny')

        acl_name = 'test'

        src_ip = self._tx_host_ip
        src_port = 'eq 5555'
        dst_ip = self._rx_host_ip
        step(str(step_num) +
             '.a Configure an ACL with 1 %s udp %s %s %s rule'
             % (action_str, src_ip, src_port, dst_ip))

        seq_num = '1'
        action = action_str
        proto = 'udp'
        dst_port = ''
        count_str = ''
        configure_acl_l3(
            self._sw1, self._aclAddrType, acl_name, seq_num, action, proto,
            src_ip, src_port, dst_ip, dst_port, count_str
        )

        apply_acl(sw=self._sw1,
                  app_type=self._aclApp,
                  interface_num=self._pri_int,
                  acl_addr_type=self._aclAddrType,
                  acl_name=acl_name,
                  direction=self._aclDir)

        step(str(step_num) + '.b Create and verify UDP packets')
        rx_expect = True if action_str == 'permit' else False
        self._create_and_verify_traffic_l3('UDP', rx_expect)

        step(str(step_num) + '.c Create and verify other UDP packets')
        rx_expect = False
        self._create_and_verify_traffic_l3('UDP', rx_expect, src_port='1000',
                                           dst_port='5555')

        step(str(step_num) + '.d Remove the configured ACL')
        unconfigure_acl(self._sw1, self._aclAddrType, acl_name)

    def acl_modify_after_sending_udp_traffic(self, step, step_num):
        self._check_basic_private_variables(step_num)

        acl_name = 'test'

        action = 'permit'
        src_ip = self._tx_host_ip
        dst_ip = self._rx_host_ip
        step(str(step_num) +
             '.a Configure an ACL with 1 %s udp %s %s rule'
             % (action, src_ip, dst_ip))

        seq_num = '1'
        proto = 'udp'
        src_port = ''
        dst_port = ''
        count_str = ''
        configure_acl_l3(
            self._sw1, self._aclAddrType, acl_name, seq_num, action, proto,
            src_ip, src_port, dst_ip, dst_port, count_str
        )

        apply_acl(sw=self._sw1,
                  app_type=self._aclApp,
                  interface_num=self._pri_int,
                  acl_addr_type=self._aclAddrType,
                  acl_name=acl_name,
                  direction=self._aclDir)

        step(str(step_num) + '.b Create and verify UDP packets')
        rx_expect = True
        self._create_and_verify_traffic_l3('UDP', rx_expect)

        step(str(step_num) + '.c Create and verify other UDP packets')
        rx_expect = False
        self._create_and_verify_traffic_l3('ICMP', rx_expect)

        action = 'deny'
        step(str(step_num) +
             '.d Modify ACL with 1 %s udp %s %s rule'
             % (action, src_ip, dst_ip))
        configure_acl_l3(
            self._sw1, self._aclAddrType, acl_name, seq_num, action, proto,
            src_ip, src_port, dst_ip, dst_port, count_str
        )

        step(str(step_num) + '.e Create and verify UDP packets')
        rx_expect = False
        self._create_and_verify_traffic_l3('UDP', rx_expect)

        step(str(step_num) + '.f Create and verify other UDP packets')
        rx_expect = False
        self._create_and_verify_traffic_l3('ICMP', rx_expect)

        step(str(step_num) + '.g Remove the configured ACL')
        unconfigure_acl(self._sw1, self._aclAddrType, acl_name)

    def acl_deny_udp_on_multiple_ports(self, step, step_num):
        self._check_basic_private_variables(step_num)
        assert self._sec_int is not None

        acl_name = 'test'

        step(str(step_num) +
             '.a Configure an ACL with a deny udp and permit icmp rule')

        seq_num = '1'
        action = 'deny'
        proto = 'udp'
        src_ip = self._tx_host_ip
        src_port = ''
        dst_ip = self._rx_host_ip
        dst_port = ''
        count_str = ''
        configure_acl_l3(
            self._sw1, self._aclAddrType, acl_name, seq_num, action, proto,
            src_ip, src_port, dst_ip, dst_port, count_str
        )

        seq_num = '2'
        action = 'permit'
        proto = 'icmp'
        src_ip = 'any'
        dst_ip = 'any'
        configure_acl_l3(
            self._sw1, self._aclAddrType, acl_name, seq_num, action, proto,
            src_ip, src_port, dst_ip, dst_port, count_str
        )

        for inter_num in (self._pri_int, self._sec_int):
            apply_acl(sw=self._sw1,
                      app_type=self._aclApp,
                      interface_num=inter_num,
                      acl_addr_type=self._aclAddrType,
                      acl_name=acl_name,
                      direction=self._aclDir)

        step(str(step_num) + '.b Create and verify UDP packets')
        rx_expect = False
        self._create_and_verify_traffic_l3('UDP', rx_expect)

        step(str(step_num) + '.c Create and verify reverse UDP packets')
        self._create_and_verify_traffic_l3('UDP', rx_expect,
                                           direction='reverse')

        step(str(step_num) + '.d Create and verify ICMP packets')
        rx_expect = True
        self._create_and_verify_traffic_l3('ICMP', rx_expect)

        step(str(step_num) + '.e Create and verify reverse ICMP packets')
        self._create_and_verify_traffic_l3('ICMP', rx_expect,
                                           direction='reverse')

        step(str(step_num) + '.f Remove the configured ACL')
        unconfigure_acl(self._sw1, self._aclAddrType, acl_name)

    def acl_permit_icmp_on_multiple_ports(self, step, step_num):
        self._check_basic_private_variables(step_num)

        acl_name = 'test'

        action = 'permit'
        src_ip = self._tx_host_ip
        dst_ip = self._rx_host_ip
        step(str(step_num) +
             '.a Configure an ACL with a %s icmp %s %s rule'
             % (action, src_ip, dst_ip))

        seq_num = '11'
        proto = 'icmp'
        src_port = ''
        dst_port = ''
        count_str = ''
        configure_acl_l3(
            self._sw1, self._aclAddrType, acl_name, seq_num, action, proto,
            src_ip, src_port, dst_ip, dst_port, count_str
        )

        apply_acl(sw=self._sw1,
                  app_type=self._aclApp,
                  interface_num=self._pri_int,
                  acl_addr_type=self._aclAddrType,
                  acl_name=acl_name,
                  direction=self._aclDir)

        # Switching the src_ip and the dst_ip
        src_ip = self._rx_host_ip
        dst_ip = self._tx_host_ip
        step(str(step_num) +
             '.b Add to an ACL with a %s icmp %s %s rule'
             % (action, src_ip, dst_ip))
        seq_num = '12'
        configure_acl_l3(
            self._sw1, self._aclAddrType, acl_name, seq_num, action, proto,
            src_ip, src_port, dst_ip, dst_port, count_str
        )

        apply_acl(sw=self._sw1,
                  app_type=self._aclApp,
                  interface_num=self._sec_int,
                  acl_addr_type=self._aclAddrType,
                  acl_name=acl_name,
                  direction=self._aclDir)

        step(str(step_num) + '.d Create and verify ICMP packets')
        rx_expect = True
        self._create_and_verify_traffic_l3('ICMP', rx_expect)

        step(str(step_num) + '.e Create and verify reverse ICMP packets')
        self._create_and_verify_traffic_l3('ICMP', rx_expect,
                                           direction='reverse')

        step(str(step_num) + '.f Remove the configured ACL')
        unconfigure_acl(self._sw1, self._aclAddrType, acl_name)

    def acl_replace_with_icmp_traffic(self, step, step_num):
        self._check_basic_private_variables(step_num)

        acl_name = 'test'

        action = 'permit'
        src_ip = self._tx_host_ip
        dst_ip = self._rx_host_ip

        seq_num = '1'
        proto = 'icmp'
        src_port = ''
        dst_port = ''
        count_str = ''

        step(str(step_num) +
             '.a Configure an ACL with a %s icmp %s %s rule'
             % (action, src_ip, dst_ip))
        configure_acl_l3(
            self._sw1, self._aclAddrType, acl_name, seq_num, action, proto,
            src_ip, src_port, dst_ip, dst_port, count_str
        )

        apply_acl(sw=self._sw1,
                  app_type=self._aclApp,
                  interface_num=self._pri_int,
                  acl_addr_type=self._aclAddrType,
                  acl_name=acl_name,
                  direction=self._aclDir)

        step(str(step_num) + '.b Create and verify ICMP packets')
        rx_expect = True
        self._create_and_verify_traffic_l3('ICMP', rx_expect)

        step(str(step_num) +
             '.c Create new ACL with a %s icmp %s %s rule and apply to same'
             'port'
             % (action, src_ip, dst_ip))
        acl_name2 = 'test2'
        action = 'deny'
        configure_acl_l3(
            self._sw1, self._aclAddrType, acl_name2, seq_num, action, proto,
            src_ip, src_port, dst_ip, dst_port, count_str
        )

        apply_acl(sw=self._sw1,
                  app_type=self._aclApp,
                  interface_num=self._pri_int,
                  acl_addr_type=self._aclAddrType,
                  acl_name=acl_name2,
                  direction=self._aclDir)

        step(str(step_num) + '.b Create and verify ICMP packets')
        rx_expect = False
        self._create_and_verify_traffic_l3('ICMP', rx_expect)

        step(str(step_num) + '.f Remove the configured ACL')
        unconfigure_acl(self._sw1, self._aclAddrType, acl_name)
        unconfigure_acl(self._sw1, self._aclAddrType, acl_name2)

    def acl_permit_icmp_hitcount(self, step, step_num):
        self._check_basic_private_variables(step_num)

        acl_name = 'test'

        action = 'permit'
        src_ip = self._tx_host_ip
        dst_ip = self._rx_host_ip
        count_str = 'count'
        step(str(step_num) +
             '.a Configure an ACL with a %s icmp %s %s %s rule'
             % (action, src_ip, dst_ip, count_str))

        seq_num = '50'
        proto = 'icmp'
        src_port = ''
        dst_port = ''
        configure_acl_l3(
            self._sw1, self._aclAddrType, acl_name, seq_num, action, proto,
            src_ip, src_port, dst_ip, dst_port, count_str
        )

        apply_acl(sw=self._sw1,
                  app_type=self._aclApp,
                  interface_num=self._pri_int,
                  acl_addr_type=self._aclAddrType,
                  acl_name=acl_name,
                  direction=self._aclDir)

        step(str(step_num) + '.b Create and verify ICMP packets')
        self._clear_hitcounts(acl_name, self._pri_int)
        self.print_clr("INFO", "Delaying 6 seconds to allow the acl to be"
                       " applied")
        sleep(6)
        rx_expect = True
        self._create_and_verify_traffic_l3('ICMP', rx_expect)

        # delay added to retrieve correct hitcount
        self.print_clr("INFO", "Delaying 6 seconds to allow the hits to show"
                       " up in the osvdb")
        sleep(6)
        assert(self._get_hitcount(sw=self._sw1, acl_name=acl_name,
                                  inter_num=self._pri_int,
                                  target_ace_str='%s %s %s %s %s count'
                                  % (seq_num, action, proto, src_ip, dst_ip))
               == '10')

        step(str(step_num) + '.f Remove the configured ACL')
        unconfigure_acl(self._sw1, self._aclAddrType, acl_name)

    def acl_config_persistance(self, step, step_num, entries_list):
        self._check_basic_private_variables(step_num)

        # Verify entries_list is a list of integers with no more than 2 entries
        # (for now).
        assert isinstance(entries_list, list) and\
            len(entries_list) <= 2
        for i in list(range(len(entries_list))):
            assert isinstance(entries_list[i], int)

        seq_num = '1'
        action = 'permit'
        proto = 'icmp'
        src_port = ''
        dst_port = ''
        count_str = 'count'

        tx_dir = ['forward', 'reverse']
        interface_num_list = [self._pri_int, self._sec_int]
        acl_name = ['test', 'test2']
        range_lt_gt_count = 0
        target_ace_list = []
        for i in list(range(len(entries_list))):
            if tx_dir[i] == 'forward':
                src_ip, dst_ip = self._tx_host_ip, self._rx_host_ip
            else:
                dst_ip, src_ip = self._tx_host_ip, self._rx_host_ip
            step(str(step_num) +
                 '.a Configure an ACL with a %s %s %s %s %s rule'
                 % (action, proto, src_ip, dst_ip, count_str))

            configure_acl_l3(
                self._sw1, self._aclAddrType, acl_name[i], seq_num, action,
                proto, src_ip, src_port, dst_ip, dst_port, count_str
            )

            step(str(step_num) +
                 '.b Configure the ACL another %s random rules (off by one'
                 ' because we already\nconfigured one rule that we will use'
                 ' for traffic tests later)' % (str(entries_list[i] - 1)))
            for seq in list(range(2, entries_list[i] + 1)):
                r_action = choice(["permit", "deny"])
                r_proto = choice([str(randrange(1, 255)), 'tcp', 'udp',
                                  'sctp'])
                r_src_ip = self._rand_ip_address_with_prefix()
                r_dst_ip = self._rand_ip_address_with_prefix()
                r_src_port, range_lt_gt_count = self._rand_port(
                                                    r_proto,
                                                    range_lt_gt_count
                                                    )
                r_dst_port, range_lt_gt_count = self._rand_port(
                                                    r_proto,
                                                    range_lt_gt_count
                                                    )
                r_count_str = choice(['count', ''])
                try:
                    configure_acl_l3(
                        self._sw1, self._aclAddrType,
                        acl_name[i], str(seq),
                        r_action, r_proto,
                        r_src_ip, r_src_port,
                        r_dst_ip, r_dst_port,
                        r_count_str
                    )
                except topology_lib_vtysh.exceptions.EchoCommandException:
                    # If the command plus the command prompt is exactly
                    # 80 characters then vtysh will echo the command back
                    # in a telnet session and confuse the vtysh library.
                    # This is a known bug.
                    pass
                except topology_lib_vtysh.exceptions.UnknownVtyshException:
                    # When the command plus the command promt is longer
                    # then 80 characters then the telnet response confuses
                    # the vtysh library. This is a known bug.
                    pass

            apply_acl(sw=self._sw1,
                      app_type=self._aclApp,
                      interface_num=interface_num_list[i],
                      acl_addr_type=self._aclAddrType,
                      acl_name=acl_name[i],
                      direction=self._aclDir)

            self._clear_hitcounts(acl_name[i], interface_num_list[i])
            step(str(step_num) + '.c Create and verify ICMP packets')
            sleep(6)
            rx_expect = True
            self._create_and_verify_traffic_l3('ICMP', rx_expect,
                                               direction=tx_dir[i])
            sleep(6)  # delay to retrieve correct hitcount
            target_ace_list.append('%s %s %s %s %s count'
                                   % (seq_num, action, proto, src_ip, dst_ip))
            assert(self._get_hitcount(sw=self._sw1, acl_name=acl_name[i],
                                      inter_num=interface_num_list[i],
                                      target_ace_str=target_ace_list[i])
                   == '10')

        # Check the running and startup config are the same before reboot
        self.print_clr("INFO", "Copying the running config to the startup"
                       " config")
        self._sw1._shells['vtysh']._timeout = 1500
        self._sw1.libs.vtysh.copy_running_config_startup_config()
        run_res_before_boot = self._sw1.libs.vtysh.show_running_config()
        start_res_before_boot = self._sw1.libs.vtysh.show_startup_config()
        assert(run_res_before_boot == start_res_before_boot)

        self.print_clr("INFO", "Rebooting Switch")
        reboot_switch(self._sw1)
        self.print_clr("INFO", "Delaying 60 seconds while switch inits")
        sleep(60)

        # Check the running config is still the same as before
        run_res_after_boot = self._sw1.libs.vtysh.show_running_config()
        assert(run_res_before_boot == run_res_after_boot)

        # Check the startup config is the same and the current running
        start_res_after_boot = self._sw1.libs.vtysh.show_startup_config()
        assert(run_res_after_boot == start_res_after_boot)

        # Check that the applied ACL still works along with logging
        step(str(step_num) + '.d Create and verify ICMP packets')
        for i in list(range(len(entries_list))):
            self._clear_hitcounts(acl_name[i], interface_num_list[i])
            rx_expect = True
            self._create_and_verify_traffic_l3('ICMP', rx_expect,
                                               direction=tx_dir[i])
            sleep(6)  # delay tp retrieve correct hitcount
            assert(self._get_hitcount(sw=self._sw1, acl_name=acl_name[i],
                                      inter_num=interface_num_list[i],
                                      target_ace_str=target_ace_list[i])
                   == '10')

        step(str(step_num) + '.e Remove the configured ACL(s)')
        for i in list(range(len(entries_list))):
            unconfigure_acl(self._sw1, self._aclAddrType, acl_name[i])
