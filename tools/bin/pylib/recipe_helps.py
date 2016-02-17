import os
import sys
import re
from contextlib import contextmanager
import ops_logging

logger = ops_logging.getLogger('recipe')

import ops_defs
sys.path.extend(ops_defs.sys_path_extensions)

import gitpkg

@contextmanager
def cd(path):
    old = os.getcwd()
    try:
        os.chdir(path)
        yield
    finally:
        os.chdir(old)


def fatal(msg):
    logger.critical('{}'.format(msg))
    sys.exit(-1)


class recipe:
    def __init__(self, all_recipes, pkg, appends=True):
        cooker = all_recipes.tinfoil.cooker

        import oe.recipeutils

        # Find top recipe file
        self.recipefile = oe.recipeutils.pn_to_recipe(cooker, pkg)
        if not self.recipefile:
            skipreasons = oe.recipeutils.get_unavailable_reasons(cooker,
                                                                 pkg)
            if skipreasons:
                fatal('\n'.join(skipreasons))
            else:
                fatal("Unable to find any recipe file matching %s" % pkg)

        # gather append files
        if appends:
            self.append_files = cooker.collection.get_file_appends(self.recipefile)
            # Filter out appends from the workspace
            self.append_files = [path for path in self.append_files if
                                 not path.startswith(all_recipes.workspace)]
        else:
            self.append_files = []
        self.all_files = [self.recipefile] + self.append_files

        self.parsed = oe.recipeutils.parse_recipe(
            self.recipefile, self.append_files, all_recipes.tinfoil.config_data)

        self.name = self.getVar('PN')

        # pre-parse git package stuff so all warning happen up front
        self.getGitPkg()

    def warn(self, msg):
        logger.warning('{}\n{}'
                       .format(msg, '\n'.join(self.all_files)))

    def fatal(self, msg):
        fatal('{}\n{}'
              .format(msg, '\n'.join(self.all_files)))

    def getVar(self, var, expand=True):
        if var == '*gitrepo*':
            # legacy support
            git_repo = self.getMainGitRepo()
            return git_repo.uri if git_repo else None
        elif var == '*gitbranch*':
            # legacy support
            git_repo = self.getMainGitRepo()
            if git_repo:
                return git_repo.branch if git_repo.branch else 'master'
            else:
                return None
        else:
            return self.parsed.getVar(var, expand=expand)

    def getGitPkg(self):
        try:
            return self._gitpkg
        except AttributeError:
            if gitpkg.is_gitpkg(self):
                self._gitpkg = gitpkg.gitpkg(self)
            else:
                self._gitpkg = None
            return self._gitpkg

    def isGitPkg(self):
        return self.getGitPkg() is not None

    def getMainGitRepo(self):
        return self.getGitPkg().repo if self.isGitPkg() else None

    def validate_for_team(self, team):
        if self.isGitPkg():
            self.getGitPkg().validate_for_team(team)

    def validate_devenv(self):
        if self.isGitPkg():
            self.getGitPkg().validate_devenv()


def ensure_recipes_readable(command=None):
    if not os.path.exists(ops_defs.local_conf):
        if command is not None:
            fatal('You must run "make devenv_init" before invoking "{}".'
                  .format(command))
        else:
                fatal('{} does not exist.\n'
                      'You must run "make devenv_init" before parsing recipes.'
                      .format(ops_defs.local_conf))



class all_recipes:
    def __init__(self, product_dirs, quiet=False,):
        ensure_recipes_readable()

        self.cache = {}

        # parse the recipes
        self.workspace = os.path.join(ops_defs.builddir, 'workspace')

        import bb.tinfoil

        if quiet:
            # Tinfoil's constructor is chatty.
            # Redirect console stderr to devnull.
            old_stderr = sys.stderr
            old_stdout = sys.stdout
            sys.stderr = open(os.devnull, 'w')
            sys.stdout = sys.stderr

        self.tinfoil = bb.tinfoil.Tinfoil(
            output=sys.stderr if quiet else sys.stdout
        )
        self.tinfoil.prepare(config_only=False)
        # import devtool
        # self.tinfoil = devtool.setup_tinfoil()

        if quiet:
            # restore console streams
            sys.stderr = old_stderr
            sys.stdout = old_stdout

        # gather list of distro packages
        s = set()
        suffix_re = re.compile(r'\.bb$')
        for proddir in product_dirs:
            for dirname, subdirs, files in os.walk(
                    os.path.join(ops_defs.yoctodir, proddir)):
                for file in files:
                    if suffix_re.search(file) and re.search(r'meta-distro',dirname):
                        name = suffix_re.sub('', os.path.basename(file))
                        # strip off version numbers
                        name = re.sub(r'_\d+(\.\d+)*$', '', name)
                        s.add(name)
        self.distro_package_names = sorted(s)


    def get(self, pkg, appends=True):
        key = (pkg, appends)
        if key not in self.cache:
            self.cache[key] = recipe(self, pkg, appends=appends)
        return self.cache[key]
