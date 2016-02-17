import os
import sys
from contextlib import contextmanager
import ops_logging

logger = ops_logging.getLogger(__name__)


import ops_defs

teamfile = os.path.join(ops_defs.topdir, 'teamdef.py')

def fatal(msg):
    logger.critical('While parsing {}\n{}'.format(teamfile, msg))
    sys.exit(-1)

@contextmanager
def no_pyc():
    old = sys.dont_write_bytecode
    try:
        sys.dont_write_bytecode = True
        yield
    finally:
        sys.dont_write_bytecode = old

def load():
    # load and validate team definition
    if not os.path.exists(teamfile):
        logger.critical('{} does not exist'.format(teamfile))
        sys.exit(-1)

    import imp
    with no_pyc():
        team = imp.load_source('teamdef', teamfile)
    try:
        return team.Team
    except AttributeError:
        fatal('missing definition of "class Team"')


def write_sample():
    if os.path.exists(teamfile):
        logger.critical('{} already exists.'.format(teamfile))
        sys.exit(-1)

    with open(teamfile, 'w') as f:
        f.write("""\
from team import TeamBase

public  = 'git.openswitch.net'

class Team(TeamBase):
    product = 'ops'
    branch_name = 'feature/pricematch'
    contact_person = 'Bob Barker'

    branched_repos = [
        # repo,                                  git server, "following" branch
        ('openswitch/ops',                           public, 'master'),
        ('openswitch/ops-cli',                       public, 'master'),
        ('openswitch/ops-openvswitch',               public, 'master'),
        ('openswitch/ops-switchd-container-plugin',  public, 'master'),
    ]

    ignored_repos = [
        # 'infra/website',
    ]
""")

    logger.info("Wrote {}".format(teamfile))


class BranchedRepodef(object):
    def __init__(self, name, host, following_branch):
        self.name = name
        self.host = host
        self.following_branch = following_branch


class TeamMetaclass(type):
    @classmethod
    def is_toplevel_class(metacls, bases):
        # If none of my bases classes instances of this metaclass,
        # I must be a top level class.
        return not any(isinstance(b, metacls) for b in bases)

    def __new__(metacls, clsname, bases, attrs):
        if metacls.is_toplevel_class(bases):
            return metacls.create_toplevel_class(clsname, bases, attrs)

        return metacls.create_derived_class(clsname,  bases, attrs)

    #----------
    @classmethod
    def create_toplevel_class(metacls, clsname, bases, attrs):
        return super(TeamMetaclass, metacls).__new__(
            metacls, clsname, bases, attrs)

    #----------
    @classmethod
    def create_derived_class(metacls, clsname, bases, attrs):
        # create the class
        cls = super(TeamMetaclass, metacls).__new__(
            metacls, clsname, bases, attrs)

        # Now validate that we have the right attributes
        cls.have_public_branches = False
        cls.have_non_public_branches = False
        try:
            if not isinstance(cls.product, basestring):
                fatal('product must be a string')

            if not cls.branch_name.startswith('feature/'):
                fatal('branch_name must start with "feature/"')

            if not isinstance(cls.contact_person, basestring):
                fatal('contact_person must be a string')

            branched_repos = cls.branched_repos
            del cls.branched_repos
            cls.branched_repodefs = {}
            for r in branched_repos:
                if (not isinstance(r, tuple)
                    or len(r) != 3
                    or not isinstance(r[0], basestring)
                    or not isinstance(r[1], basestring)
                    or not isinstance(r[2], basestring)):
                    fatal('branched_repos must be a list of (string, string, string) tuples')
                cls.branched_repodefs[r[0]] = BranchedRepodef(*r)

                if r[1] == ops_defs.ops_githost:
                    cls.have_public_branches = True
                else:
                    cls.have_non_public_branches = True
                    if cls.product == 'ops':
                        fatal('{} has a git server other than "{}".\n'
                              'Not allowed when product == "ops"'
                              .format(r[0], ops_defs.ops_githost))

            if cls.product != 'ops' and not cls.have_non_public_branches:
                fatal('All repos use git server "{}".\n'
                      'Product should be "ops"'
                      .format(ops_defs.ops_githost))

            if hasattr(cls, 'ignored_repos'):
                ignored_repos = cls.ignored_repos
                del cls.ignored_repos
                cls.ignored_repos = {}
                for r in ignored_repos:
                    if not isinstance(r, basestring):
                        fatal('ignored_repos should be a list of strings')
                    cls.ignored_repos[r] = True
            else:
                cls.ignored_repos = {}


        except AttributeError as e:
            fatal(e.message)
        return cls


class TeamBase(object):
    __metaclass__ = TeamMetaclass

    @classmethod
    def get_branched_repodef(cls, reponame):
        return cls.branched_repodefs.get(reponame, None)
