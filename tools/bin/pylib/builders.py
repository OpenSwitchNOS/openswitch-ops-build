import os
import sys
import re

import ops_defs
import ops_logging
from git import git, SmartUrl

logger = ops_logging.getLogger(__name__)


def fatal(msg):
    logger.critical(msg)
    sys.exit(-1)


def slurp_keyvalue_conf_file(fname):
    """Slurp a file that looks like:
# comment
# comment

key value
key value   # comment
key value

    And return its key/value pairs in a dict.
    """
    cfg = {}
    with open(fname) as FILE:
        for line in FILE.readlines():
            if re.match(r'\s*#', line):
                continue  # skip lines that are only comments

            # strip trailing comments & then leading/trailing whitespace
            line = re.sub(r'#.*$', '', line).strip()

            # skip lines that are now complete0ly blank
            if len(line) == 0:
                continue

            k, v = line.split()
            cfg[k] = v
    return cfg


def slurp_value_per_line_conf_file(fname):
    """Slurp a file that looks like:
# comment
# comment

value
value   # comment
value

    And return its values in a list.
    """
    cfg = []
    with open(fname) as FILE:
        for line in FILE.readlines():
            if re.match(r'\s*#', line):
                continue  # skip lines that are only comments

            # strip trailing comments & then leading/trailing whitespace
            line = re.sub(r'#.*$', '', line).strip()

            # skip lines that are now complete0ly blank
            if len(line) == 0:
                continue

            cfg.append(line)
    return cfg


class BuilderBase(object):
    def __init__(self, team, topdir):
        from git import git
        self.team = team
        self.topdir = topdir
        self.topgit = git(self.topdir)
        self.is_configured = os.path.exists(os.path.join(self.topdir,
                                                         '.platform'))
        self.is_devenv_inited = os.path.exists(os.path.join(self.topdir,
                                                            '.devenv'))

    def validate_top_branch(self):
        if self.topgit.cur_branch() != self.team.branch_name:
            fatal('Current branch for {} must be {}.\n'
                  'Required because Team.branch_name == "{}".\n'
                  'The current branch is {}.'
                  .format(self.topdir, self.team.branch_name,
                          self.team.branch_name, self.topgit.branch))

    def get_all_recipes(self):
        try:
            return self._all_recipes
        except AttributeError:
            import recipe_helps
            self._all_recipes = recipe_helps.all_recipes(self.product_dirs(),
                                                         quiet=False)
            return self._all_recipes

    def validate_devenv_added_pkgs(self):
        assert self.is_devenv_inited
        devenv_added = slurp_value_per_line_conf_file(os.path.join(self.topdir,
                                                            '.devenv'))
        if devenv_added:
            all_recipes = self.get_all_recipes()
            for pkgname in devenv_added:
                all_recipes.get(pkgname).validate_devenv()


    def validate_pkg_recipes_for_team(self, team):
        # TODO: See if we can move this to before devenv_init
        #
        # The missing local.conf currently prevents us from parsing
        # the recipes before "make devenv_init". Since we can do a
        # bare "make" w/o first doing a "make devenv_init", there must
        # be a way to parse the recipes without it.
        #
        # Figure out what a bare "make" does and do the same.
        assert self.is_devenv_inited

        all_recipes = self.get_all_recipes()
        for pkgname in all_recipes.distro_package_names:
            all_recipes.get(pkgname).validate_for_team(team)



class OpsBuilder(BuilderBase):
    """Used for workspaces cloned directly from ops-build."""
    def __init__(self, team, topdir):
        super(OpsBuilder, self).__init__(team, topdir)
        assert self.team.product == 'ops'

    def product_dirs(self):
        return ('openswitch')

    def post_clone_validate(self, team):
        # make sure we cloned from the right host and repo
        if (self.topgit.origin.hostname != ops_defs.ops_githost
            or
            self.topgit.origin.relpath != ops_defs.ops_top_gitrepo):
            fatal('{} must be cloned from {}/{}.\n'
                  'Required because Team.product == "ops"\n'
                  'It is currently cloned from {}/{}'
                  .format(self.topdir,
                          ops_defs.ops_githost, ops_defs.ops_top_gitrepo,
                          self.topgit.origin.hostname,
                          self.topgit.origin.relpath))

        self.validate_top_branch()


    def post_configure_validate(self, team):
        logger.warning('NOT_IMPLEMENTED: OpsBuilder.post_configure_validate')


    def post_devenv_init_validate(self, team):
        self.validate_pkg_recipes_match_teamdef(team)
        self.validate_devenv_added_pkgs()


    def validate_merge_base(self, team):
        logger.warning('NOT_IMPLEMENTED: OpsBuilder.validate_merge_base')



class NestedBuilder(BuilderBase):
    """Used for workspaces have ops-build cloned into a subdirectory."""
    def __init__(self, team, topdir):
        super(NestedBuilder, self).__init__(team, topdir)
        assert self.team.product != 'ops'
        self.recipe_group_defs = {}

    def product_dirs(self):
        return self.recipe_group_defs.keys()

    def post_clone_validate(self, team):
        # see if the repo we cloned defines the product we want
        master_conf_path = os.path.join(self.topdir, 'config',
                                        self.team.product, 'master.conf')
        repos_conf_path = os.path.join(self.topdir, 'config', 'repos.conf')

        if not os.path.exists(master_conf_path):
            fatal('{} not found\n'
                  'Required because Team.product == "{}"\n'
                  '{} is cloned from {}'
                  .format(master_conf_path, self.team.product,
                          self.topdir, self.topgit.origin.relpath))


        # we can't validate the topdir's githost, it could be anything

        self.validate_top_branch()

        # read conf files to figure out which host/repo/branch we should
        # use for all our recipe repos
        master_cfg = slurp_keyvalue_conf_file(master_conf_path)
        repos_cfg = slurp_keyvalue_conf_file(repos_conf_path)
        for group, branch in master_cfg.iteritems():
            if group == 'openswitch':
                relpath = 'openswitch'
            else:
                relpath = os.path.join('openswitch', 'yocto', group)
            self.recipe_group_defs[group] = {
                'branch': branch,
                'giturl': SmartUrl(repos_cfg[group]),
                'path':   os.path.join(self.topdir, relpath)
            }
            if branch != self.team.branch_name:
                fatal('Branch entry for {} must be {} (instead of {})\n'
                      'In {}\n'
                      'Reason: When working in team environment all recipe repos must be branched.'
                      .format(group, self.team.branch_name,
                              self.recipe_group_defs[group]['branch'],
                              master_conf_path))

        # validate the we have the minimum set we need
        for group in ('openswitch', self.team.product):
            if group not in self.recipe_group_defs:
                fatal('{} does not have an entry for {}'
                      .format(master_conf_path, group))


    def post_configure_validate(self, team):
        for group_name, group in self.recipe_group_defs.iteritems():
            path = group['path']
            if not os.path.isdir(path):
                fatal('Recipe repo {} is missing.\n'
                      'Even though it appear that {} has been configured'
                      .format(path, self.topdir))
            repo = git(path)
            url = group['giturl']
            group['git'] = repo
            if (repo.origin.hostname != url.hostname
                or
                repo.origin.relpath != url.relpath):
                fatal('{} recipes must be cloned from {}/{}.\n'
                      'Required because of repos.conf\n'
                      'It is currently cloned from {}/{}\n'
                      'Workdir = {}'
                      .format(group_name, url.hostname, url.relpath,
                              repo.origin.hostname, repo.origin.relpath,
                              path))
            if repo.cur_branch() != group['branch']:
                fatal('{} recipes current branch must be {} (instead of {}).\n'
                      'Required because of master.conf\n'
                      'Workdir = {}'
                      .format(group_name, group['branch'], repo.cur_branch(),
                              path))
            repo.validate_gerrit(default_branch=group['branch'])


    def post_devenv_init_validate(self, team):
        self.validate_pkg_recipes_for_team(team)
        self.validate_devenv_added_pkgs()


    def validate_merge_base(self, team):
        logger.warning('NOT_IMPLEMENTED: NestedBuilder.validate_merge_base')
        logger.warning('     verify saved SHA file exists')
        logger.warning('     foreach recipe repo:')
        logger.warning('          use git working to extract merge base')
        logger.warning('          verify it matches merge base from saved SHAs')
        logger.warning('     foreach devenv_add\'d pkg:')
        logger.warning('         foreach repo in pkg:')
        logger.warning('             if want branched:')
        logger.warning('                 verify cur_branch == team.feature_branch')
        logger.warning('                 use git working to extract merge base')
        logger.warning('                 verify it matches merge base from saved SAHS')
        logger.warning('     foreach branched repo not in devenv_add\'d pkg:')
        logger.warning('          HOW DO WE GET merge-base')
        logger.warning('          verify it matches merge base from saved SHAs')
