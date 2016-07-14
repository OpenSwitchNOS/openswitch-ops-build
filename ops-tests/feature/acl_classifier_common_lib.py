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


from re import search, findall
from topology_lib_scapy.library import ScapyThread, send_traffic, sniff_traffic
from time import sleep
from ipaddress import ip_address, IPv4Network, NetmaskValueError
from datetime import datetime
import re

from acl_protocol_names import get_ipv4_protocol_name


"""
Use Case for Library Functions:
    filter_str = (
                    "lambda p: ICMP in p and p[IP].src == '10.0.10.1' "
                    "and p[IP].dst == '10.0.10.2'"
                )

    configure_acl_l3(
                    ops1, 'ip', 'test', '40', 'permit', 'any', '10.0.10.1',
                    '', '10.0.10.2', '', ''
                )

    apply_acl(ops1, 'port', '1', 'ip', 'test', 'in')

    create_and_verify_traffic(
            topology, hs1, hs2, '10.0.10.1',
            '', '10.0.10.2', '', 'IP/ICMP',
            filter_str, 10, True
            )

    unconfigure_acl(ops1, 'test')
"""


def configure_acl_l3(
            sw, acl_addr_type, acl_name, seq_num, action, proto, src_ip,
            src_port, dst_ip, dst_port, *count_log_str
        ):
    """
    Configure an ACL with one permit or one deny rule
    """

    assert sw is not None
    assert acl_addr_type in ('ip')  # Will add ipv6 in future, but not mac
    assert isinstance(acl_name, str)
    assert isinstance(seq_num, str)
    assert action in ('permit', 'deny')
    assert isinstance(proto, str)
    assert isinstance(src_ip, str)
    assert isinstance(src_port, str)
    assert isinstance(dst_ip, str)
    assert isinstance(dst_port, str)

    count_str = ''
    log_str = ''
    display_str = ''

    for arg in count_log_str:
        if arg == 'count':
            count_str = 'count'
            if display_str == '':
                display_str = 'count'
        elif arg == 'log':
            log_str = 'log'
            display_str = 'log'

    if acl_addr_type == 'ip':
        with sw.libs.vtysh.ConfigAccessListIpTestname(acl_name) as ctx:
            getattr(ctx, action)(
                          '',
                          seq_num, proto, src_ip, src_port,
                          dst_ip, dst_port, count_str, log_str
                          )

        ace_args = [seq_num, action, get_ipv4_protocol_name(proto),
                    tailor_ip_addr_for_show_run(src_ip), src_port,
                    tailor_ip_addr_for_show_run(dst_ip), dst_port,
                    display_str]
        ace_str = ' '.join(args for args in ace_args)
        print('action_line_str is {}'.format(ace_str))
    else:
        # TODO: add ipv6 here
        assert False

    ace_re = re.compile(re.sub('\s+', '\s+', ace_str.strip()))
    test_result = sw('show run')
    assert re.search(ace_re, test_result)


def apply_acl(sw, app_type, interface_num, acl_addr_type, acl_name, direction):
    """
    Apply ACL on interface in ingress or egress direction
    """
    assert sw is not None
    assert app_type in ('port', 'vlan')  # Will add tunnel in future
    assert acl_addr_type in ('ip', 'ipv6', 'mac')

    # If the app_type is port, then interface_num is the port number the acl
    # should be applied to.  If vlan, then the VLAN number.  If tunnel, then
    # tunnel number.
    assert isinstance(interface_num, str)
    assert isinstance(acl_name, str)
    assert direction in ('in', 'out')

    if app_type == 'port':
        if direction == 'in':
            with sw.libs.vtysh.ConfigInterface(interface_num) as ctx:
                ctx.apply_access_list_ip_in(acl_name)
        elif direction == 'out':
            with sw.libs.vtysh.ConfigInterface(interface_num) as ctx:
                ctx.apply_access_list_ip_out(acl_name)
        else:
            # Undefined direction
            assert(False)
    else:
        # Undefined ACL application type
        assert(False)

    apply_line_re = re.compile(
                        'apply\s+access-list\s+%s\s+%s\s+%s'
                        % (acl_addr_type, acl_name, direction)
                    )

    test_result = sw('show run')
    assert re.search(apply_line_re, test_result)


def unconfigure_acl(sw, acl_addr_type, acl_name):
    """
    Remove an ACL
    """
    assert sw is not None
    assert acl_addr_type in ('ip', 'ipv6', 'mac')
    assert isinstance(acl_name, str)

    if acl_addr_type == 'ip':
        with sw.libs.vtysh.Configure() as ctx:
            ctx.no_access_list_ip(acl_name)
    else:
        # ipv6 and mac not implemented yet
        assert False

    test_result = sw('show run')
    assert search(r'(access-list\s+{acl_addr_type}\s+{acl_name}(?!\S))'
                  .format(**locals()), test_result) is None


def create_and_verify_traffic(
                        topology, tx_host, rx_host, src_ip,
                        src_port, dst_ip, dst_port, proto_str,
                        filter_str, tx_count, rx_expect
                        ):
    assert topology is not None
    assert tx_host is not None
    assert rx_host is not None
    assert isinstance(src_ip, str)
    assert isinstance(dst_ip, str)
    assert isinstance(src_port, str)
    assert isinstance(dst_port, str)
    assert isinstance(proto_str, str) and \
        proto_str in ('IP/UDP', 'IP/ICMP')

    # The filter_str is expected to be a string.  Below is an example for a
    # UDP packet:
    # filter_udp = "lambda p: UDP in p and p[UDP].dport == 48621 and " \
    #   "p[IP].src == '1.1.1.1' and p[IP].dst == '1.1.1.2'"
    assert isinstance(filter_str, str)
    assert isinstance(tx_count, int)
    assert isinstance(rx_expect, bool)

    ip_packet = tx_host.libs.scapy.ip("dst='%s', src='%s'" % (dst_ip, src_ip))

    if proto_str == 'IP/UDP':
        proto_packet = tx_host.libs.scapy.udp()
        if dst_port != '':
            proto_packet['dport'] = int(dst_port)
        if src_port != '':
            proto_packet['sport'] = int(src_port)
        result_index = 1
    elif proto_str == 'IP/ICMP':
        proto_packet = tx_host.libs.scapy.icmp()
        result_index = 2
    else:
        assert False

    list1 = [ip_packet, proto_packet]
    port_str = '1'
    timeout = 25

    txthread = ScapyThread(
                send_traffic,
                tx_host.identifier, topology, proto_str, list1, '', tx_count,
                '', 0)

    rxthread = ScapyThread(
                sniff_traffic,
                rx_host.identifier, topology, '', [], filter_str, tx_count,
                port_str, timeout)

    rxthread.start()
    txthread.start()

    txthread.join()
    rxthread.join()

    if rxthread.outresult():
        rest, sniffcnt = rxthread.outresult().split('<Sniffed:')
        list_result = findall(r'[0-9]+', sniffcnt)

        assert (rx_expect == (list_result[result_index] == str(tx_count)))
    else:
        assert False


def wait_until_interface_up(switch, portlbl, timeout=30, polling_frequency=1):
    """
    Wait until the interface, as mapped by the given portlbl, is marked as up.

    :param switch: The switch node.
    :param str portlbl: Port label that is mapped to the interfaces.
    :param int timeout: Number of seconds to wait.
    :param int polling_frequency: Frequency of the polling.
    :return: None if interface is brought-up. If not, an assertion is raised.
    """
    for i in range(timeout):
        status = switch.libs.vtysh.show_interface(portlbl)
        if status['interface_state'] == 'up':
            break
        sleep(polling_frequency)
    else:
        assert False, (
            'Interface {}:{} never brought-up after '
            'waiting for {} seconds'.format(
                switch.identifier, portlbl, timeout
            )
        )


def reboot_switch(switch, shell='vtysh', silent=False, onie=False):
    """
    Reboot the switch
    :param topology_ostl.nodes.Switch switch: the switch node
    :param str shell: shell to use to perfom the reboot
    :param bool silent: suppress output if true.
    :param bool onie: reboot to the onie rescue prompt if true
    """

    if not silent:
        print('{} [{}].reboot_switch(onie=\'{}\', shell=\'{}\') ::'.format(
            datetime.now().isoformat(), switch.identifier, onie, shell
        ))

    if shell == "bash":
        _shell = switch.get_shell('bash')
        _shell.send_command(
            'reboot', matches=r'Restarting system.', timeout=300)

    elif shell == "vtysh":
        _shell = switch.get_shell('vtysh')
        _shell.send_command(
            'reboot', matches=r'\r\nDo you want to continue [y/n]?')
        _shell.send_command('y', matches=r'Restarting system.', timeout=300)

    elif shell == "onie":
        _shell = switch.get_shell('bash')
        _spawn = _shell._get_connection('0')
        _spawn.sendline('reboot')
        _spawn.expect(r'The system is going down NOW!', timeout=300)

    else:
        raise Exception(
            'Shell {} reboot command is not supported.'.format(shell)
        )

    login_switch(switch, onie=onie)


def login_switch(switch, onie=False):
    """
    Login to the switch
    :param topology_ostl.nodes.Switch switch: the switch node
    :param bool onie: login to the onie rescue prompt if true
    """

    _shell = switch.get_shell('bash')
    _spawn = _shell._get_connection('0')

    if(onie):
        expect_matches = [
            r'\*OpenSwitch.*',
            r'\*ONIE: Install OS.*',
            r'\*ONIE[^:].*',
            r'\*ONIE: Rescue.*',
            r'\r\nPlease press Enter to activate this console.',
            r'\r\nONIE:/\s+#'
        ]

        for num in range(10):
            index = _spawn.expect(expect_matches, timeout=300)
            if (index == 0 or index == 1):
                _spawn.send('v')
            elif (index == 2 or index == 3 or index == 4):
                _spawn.send('\r')
            elif index == 5:
                break
    else:
        expect_matches = [
            r'(?<!Last )login:\s*$',
            r'\r\nroot@[-\w]+:~# ',
            r'\r\n[-\w]+(\([-\w\s]+\))?#'
        ]

        _spawn.sendline('')
        for num in range(10):
            sleep(0.5)
            index = _spawn.expect(expect_matches, timeout=300)
            if index == 0:
                _spawn.sendline('root')
            elif index == 1:
                _spawn.sendline('vtysh')
            elif index == 2:
                break


def tailor_ip_addr_for_show_run(ip_str):
    # When configuring an ACE the user can use the prefix form /<nbits> or
    # /w.x.y.z, but show run only outputs the /w.x.y.z prefix specification.
    # This function tailors the ip_str in to the 'show run' form if not already
    assert(ip_str is not None and isinstance(ip_str, str) and ip_str != '')

    if ip_str == 'any' or ip_str.find('/') == -1:
        return ip_str
    else:
        ip_obj = ip_address(ip_str[0: ip_str.find('/')])
        if (ip_obj.version == 4):
            try:
                ip_net_obj = IPv4Network(ip_str)
            except NetmaskValueError:
                # IPv4Network class cannot handle non contiguous subnet masks
                # like 255.0.255.255.
                return ip_str

            if ip_net_obj.prefixlen == 32:
                # Just ip address, no netmask
                return ip_net_obj.network_address
            else:
                # ip address with netmask in the form a.b.c.d/w.x.y.z
                return ip_net_obj.with_netmask
        else:
            # IPv6 not implemented yet
            assert False
