import os
import sys

ops_githost = 'git.openswitch.net'
ops_top_gitrepo = 'openswitch/ops-build'

# Determine necessary python include paths
# We want these to be relative to where this python module lives and not
# relative to BUILDDIR.
_tmpdir = os.path.realpath(os.path.dirname(os.path.realpath(__file__)))
yoctodir = os.path.join(_tmpdir, 'yocto')
while not os.path.isdir(yoctodir):
    _newdir = os.path.dirname(_tmpdir)
    if _newdir == _tmpdir:
        sys.exit("INTERNAL ERROR: Unable to guess yoctodir.")
    _tmpdir = _newdir
    yoctodir = os.path.join(_tmpdir, 'yocto')
pokydir = os.path.join(yoctodir, 'poky')
sys_path_extensions = [
    os.path.join(pokydir, 'bitbake', 'lib'),  # bb
    os.path.join(pokydir, 'meta',    'lib'),  # oe
    os.path.join(pokydir, 'scripts', 'lib'),  # devtool
]

# get or guess the TOPDIR
builddir = os.environ.get('BUILDDIR')
if builddir:
    topdir = os.path.dirname(os.path.realpath(builddir))

    # validate that this really is an openswitch topdir
    if not os.path.exists(os.path.join(topdir, 'yocto')):
        sys.exit("$BUILDDIR is set, but $BUILDDIR/../yocto doesn't exist")
else:
    # start with parent of yoctodir (from above)
    topdir = os.path.dirname(yoctodir)

    # now check if topdir's parent also has a 'yocto' dir that is a symlink
    # the the one at this level
    _tmpdir = os.path.dirname(topdir)
    if _tmpdir != topdir: # catch corner case where topdir = /
        _tmpyocto = os.path.join(_tmpdir, 'yocto')
        if os.path.exists(_tmpyocto) and os.path.realpath(_tmpyocto) == yoctodir:
            topdir = _tmpdir


# Now set BUILDDIR based on our discovered top
# It's OK if it doesn't exist yet.
builddir = os.path.join(topdir, 'build')
os.environ['BUILDDIR'] = builddir
os.environ['BBPATH'] = builddir


local_conf = os.path.join(builddir, 'conf', 'local.conf')
