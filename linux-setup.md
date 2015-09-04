# Setting up a Linux machine for OpenSwitch Development
OpenSwitch requires a Linux-based system in order to contribute to the source code and build OpenSwitch images. If a contributor wishes to add to the OpenSwitch documentation only, a Windows-based OS can be used. Refer to the [How to contribute to the OpenSwitch documentation in Windows](./windows-setup.html) guide for instructions.

In order to contribute to the OpenSwitch project or to be able to build an OpenSwitch image, use the following  instructions to configure your Linux based system:

## Contents
- [Define proxies](#define-proxies)
- [Installing the required packages](#installing-the-required-packages)
  - [Ubuntu](#ubuntu)
  - [Debian](#debian)
  - [Fedora](#fedora)
  - [CentOS](#centos)

## Define proxies
If defining proxies is not needed, proceed to *"Installing the required packages"*.
1. Define your proxies if in a network that uses a proxy to reach the Internet.  For example:
```bash
$ export http_proxy=http://<proxy-url>:<proxy-port>
$ export https_proxy=https://<proxy-url>:<proxy-port>
$ export ftp_proxy=ftp://<proxy-url>:<proxy-port>
```
2. Add the export commands to a location where they will be set up automatically, for example at `~/.bashrc`.

## Installing the required packages
The packages required vary depending on the Linux distributions. Run the installation commands for your distribution.

### Ubuntu
```bash
$ sudo apt-get install gawk wget git-core diffstat unzip texinfo gcc-multilib  build-essential chrpath screen curl device-tree-compiler libsdl1.2-dev xterm
```
If you encounter an error using `libsdl1.2`, the following commands can be used instead:
```bash
$ sudo apt-get install gawk wget git-core diffstat unzip texinfo gcc-multilib  build-essential chrpath screen curl device-tree-compiler xterm aptitude
$ sudo aptitude install libsdl1.2-dev
```
### Debian
```bash
$ sudo apt-get install gawk wget git-core diffstat unzip texinfo gcc-multilib  build-essential chrpath screen curl device-tree-compiler libsdl1.2-dev xterm gperf quilt
```
### Fedora
```bash
$ sudo yum install gawk make wget tar bzip2 gzip python unzip perl patch diffutils diffstat git cpp gcc gcc-c++ \
    glibc-devel texinfo chrpath ccache perl-Data-Dumper perl-Text-ParseWords perl-Thread-Queue SDL-devel xterm screen dtc
```
### CentOS
```bash
$ sudo yum install gawk make wget tar bzip2 gzip python unzip perl patch \
     diffutils diffstat git cpp gcc gcc-c++ glibc-devel texinfo chrpath SDL-devel xterm glibc-devel.i686 screen dtc
```
Some CentOS distributions ship Python 2.6.6 (or older) which is incompatible with Yocto. Several system utilities, such as `yum`, break if the default Python interpreter is upgraded. To avoid this situation do the following:
1. Install a pre-built standalone Python for Yocto with the following commands:
```bash
cd /tmp
wget http://downloads.yoctoproject.org/releases/yocto/yocto-1.6.1/buildtools/poky-eglibc-x86_64-buildtools-tarball-core2-64-buildtools-nativesdk-standalone-1.6.1.sh
chmod +x poky-eglibc-x86_64-buildtools-tarball-core2-64-buildtools-nativesdk-standalone-1.6.1.sh
sudo ./poky-eglibc-x86_64-buildtools-tarball-core2-64-buildtools-nativesdk-standalone-1.6.1.sh -y
```
2. Set up the environment:
```bash
source /opt/poky/1.6.1/environment-setup-x86_64-pokysdk-linux
```