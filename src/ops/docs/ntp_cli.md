
NTP Commands Reference
=======================

- [NTP Configuration Commands](#ntp-configuration-commands)
	- [ntp server](#ntp server)
	- [ntp authentication enable](#ntp authentication enable)
	- [ntp authentication key](#ntp authentication-key)
	- [ntp trusted key](#ntp trusted-key)
- [Display Commands](#display-commands)
	- [show ntp associations](#show-ntp-associations)
	- [show ntp status](#show-ntp-status)
	- [show ntp statistics](#show-ntp-statistics)
	- [show ntp trusted-keys](#show-ntp-trusted-keys)

## NTP Configuration Commands

### ntp server

#### Syntax
```
ntp server <name|ipv4-address> [key key-id] [prefer] [version version-number]
[no] ntp server <name|ipv4-address>
```

#### Description
Forms an association with an NTP server.

#### Authority
admin

#### Parameters
| Parameter | Status   | Syntax |	Description          |
|-----------|----------|----------------------|
| *name* | Required | name-string of max-len 57 chars or A.B.C.D | name or IPV4 address of the server |
| *key-id* | Optional | 1-65534 | Key used while communicating with the server |
| *prefer* | Optional | Literal | Request to make this the preferred NTP server |
| *version-no* | Optional | 3-4 | NTP version 3 or 4 |
| **no** | Optional | Literal | Destroys a previously configured server |

#### Examples
```
s1(config)#ntp server time.microsoft.com key 10 version 4
s1(config)#no ntp server 192.0.1.1
```

### ntp authentication

#### Syntax
```
[no] ntp authentication enable
```

#### Description
Enables/disables the NTP authentication feature

#### Authority
admin

#### Parameters
| Parameter | Status   | Syntax |	Description          |
|-----------|----------|----------------------|
| **no** | Optional | Literal | Disables the NTP authentication feature |

#### Examples
```
s1(config)#ntp authentication enable
s1(config)#no ntp authentication enable
```

### ntp authentication-key

#### Syntax
```
ntp authentication-key <key-id> md5 <password>
[no] ntp authentication-key <key-id>
```

#### Description
Define the authentication key

#### Authority
admin

#### Parameters
| Parameter | Status   | Syntax |	Description          |
|-----------|----------|----------------------|
| *key-id* | Required | 1-65534 | Key used while communicating with the server |
| password | Required | 8-16 chars | MD5 password |
| **no** | Optional | Literal | Destroys the perviously created NTP auth key |

#### Examples
```
s1(config)#ntp authentication-key 1 md5 myPassword
s1(config)#no ntp authentication-key 1
```

### ntp trusted-key

#### Syntax
```
[no] ntp trusted-key <key-id>
```

#### Description
Mark a previously defined authentication key as trusted.
If NTP authentication is enabled, the device will synchronize with a time source only if the server carries one of the authentication keys
specified as a trusted key

#### Authority
admin

#### Parameters
| Parameter | Status   | Syntax |	Description          |
|-----------|----------|----------------------|
| *key-id* | Required | 1-65534 | Key used while communicating with the server |
| password | Required | 8-16 chars | MD5 password |
| **no** | Optional | Literal | Destroys the perviously created NTP auth key |

#### Examples
```
s1(config)#ntp authentication-key 1 md5 myPassword
s1(config)#no ntp authentication-key 1
```

##Display Commands

### show ntp associations

#### Syntax
```
show ntp associations
```

#### Description
Displays status of connections to NTP servers

#### Authority
admin

#### Parameters
None

#### Examples
```
s1(config)#show ntp associations
=======================================================================================================
Name      ref-id  prefer  VER  remote           stratum  type  last  poll  reach  delay  offset  jitter
=======================================================================================================
192.0.1.1 .INIT.  1       4                     2        U     10    64    0      0.121  0.000   0.000
```

#### Key
```
name    : NTP server FQDN/IPV4 address
ref-id  :
prefer  :
VER     : NTP version (3 or 4)
remote  :
stratum : number of hops between the client and the reference clock.
type    : Transmission Type - U unicast; B Broadcast; L Local.
last    : poll interval since the last packet was received (seconds unless unit is provided).
poll    : interval between NTP poll packets. Maximum (1024) reached as server and client syncs.
reach   : octal number that displays status of last eight NTP messages (377 - all messages received).
delay   : round trip delay of packets to the selected reference clock.
offset  : difference between local clock and reference clock.
jitter  : maximum error of local clock relative to the reference clock.
```

### show ntp associations

#### Syntax
```
show ntp status
```

#### Description
Displays the status of NTP on the switch. Whether NTP is enabled/disabled & whether it has been syncronized with a server.

#### Authority
admin

#### Parameters
None

#### Examples
```
s1(config)#show ntp status
NTP is enabled.
Synchronized to NTP Server 17.253.2.253 at stratum 1.
Poll interval = 1024 seconds.
Time accuracy is within 50 ms.
```

### show ntp authentication-keys

#### Syntax
```
show ntp authentication-keys
```

#### Description
Displays the NTP authentication keys

#### Authority
admin

#### Parameters
None

#### Examples
```
s1(config)#show ntp authentication-keys
--------------------------------
Auth key            MD5 password
--------------------------------
 10                 MyPassword
```

### show ntp trusted-keys

#### Syntax
```
show ntp trusted-keys
```

#### Description
Displays the NTP trusted keys

#### Authority
admin

#### Parameters
None

#### Examples
```
s1(config)#show ntp trusted-keys
--------------------------------
Trusted keys
--------------------------------
 10 50
```

### show ntp statistics

#### Syntax
```
show ntp statistics
```

#### Description
Displays the global NTP statistics

#### Authority
admin

#### Parameters
None

#### Examples
```
s1(config)#show ntp statistics
=========================================================================================================================================
Uptime   Rx-pkts   Rx cur ver   Rx old ver   Err-pkts   Auth-failed-pkts   Declined-pkts   Restricted-pkts   Rate-limited-pkts   KoD-pkts
=========================================================================================================================================
 12 h    100       80           20           2          1                  0               0                 0                   0
=========================================================================================================================================
```
