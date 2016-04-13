#Enables auditing blacklisted files and  directories. A message is sent to syslog in case the file or the directory is accessed
tracelog

#Run program directly without user shell
shell none

#Keep Only capabilities needed
caps.keep net_admin,net_bind_service,audit_write

#Create Private Directories for dev and tmp and etc
private-dev
private-tmp
private-etc group,shadow,pam.conf,pam.d,hostname,localtime,nsswitch.conf,passwd,resolv.conf,ssl/certs/server.crt,ssl/certs/server-private.key

#set files to Read Only
read-only /etc/ssl/certs/server.crt
read-only /etc/ssl/certs/server-private.key

read-only /usr/share/openvswitch/configdb.ovsschema
read-only /usr/share/openvswitch/vswitch.ovsschema
read-only /usr/share/openvswitch/vswitch.extschema
read-only /usr/share/openvswitch/vswitch.xml

#Use to join a firejail session to debug
name restd
