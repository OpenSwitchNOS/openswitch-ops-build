import os
import sys
import re
import inspect
import urlparse

import ops_defs
import ops_logging

logger = ops_logging.getLogger(__name__)


class gitpkg_repodef(object):
    """
    Represents a single git repo extracted from a SRC_URI.

    Contains information about host, branch, destsuffix, etc.
    """
    def __init__(self, srcuri_piece, recipe):
        self.recipe = recipe

        parts = re.split(r';', srcuri_piece)
        self.uri = parts.pop(0)

        # turn all the ;foo=bar;a=b stuf at the end into a dict
        self.extras = {}
        for part in parts:
            key, value = re.split(r'=', part)
            self.extras[key] = value

        # we use branch a lot, pull it into a member
        self.branch = self.extras.get('branch', None)

        # if protocol is set, use it to modify the uri
        if 'protocol' in self.extras:
            self.uri = re.sub(r'^git(sm)?', self.extras['protocol'], self.uri)

        # process destsuffix (illegal for git://, provides subdir gor gitsm://)
        issub = srcuri_piece.startswith('gitsm://')
        if issub:
            self.subdir = re.sub('^git/', '', self.extras.get('destsuffix', ''))
            if self.subdir == '':
                recipe.fatal('gitsm:// in SRC_URI without a destsuffix')
        else:
            if 'destsuffix' in self.extras:
                recipe.fatal('git:// in SRC_URI with a destsuffix')
            self.subdir = None

        # get the SRCREV from the appropriate variable
        name = self.extras.get('name', None)
        if name:
            self.srcrevkey = 'SRCREV_' + name
        else:
            self.srcrevkey = 'SRCREV'
        self.srcrev = recipe.getVar(self.srcrevkey)

        # extract pieces from uri for easier verification later
        parsed = urlparse.urlparse(self.uri)
        self.hostname = parsed.hostname
        self.reponame = parsed.path
        self.reponame = re.sub(r'^/', '', self.reponame)
        self.reponame = re.sub(r'\.git$', '', self.reponame)


    def validate_for_team(self, team):
        branched_repodef = team.get_branched_repodef(self.reponame)
        fatal_errors = []
        warnings = []
        if branched_repodef:
            # the team is branching this repo
            if not self.branch:
                fatal_errors.append('Missing branch={}'
                                    .format(team.branch_name))
            elif self.branch != team.branch_name:
                fatal_errors.append('Incorrect branch={}. Should be branch={}'
                                    .format(self.branch, team.branch_name))
            if self.srcrev != 'AUTOINC':
                fatal_errors.append('Incorrect {}={}. Should be {}={}'
                                    .format(self.srcrevkey, self.srcrev,
                                            self.srcrevkey, '${AUTOREV}'))
            if self.hostname != branched_repodef.host:
                fatal_errors.append('Incorrect git host {}. Should be {}'
                                    .format(self.hostname,
                                            branched_repodef.host))
            reason = "{} is one of the team's branched repos.".format(
                self.reponame)
        elif self.reponame not in team.ignored_repos:
            # the team is not branching nor ignoring  this repo
            if self.branch:
                warnings.append(
                    'Incorrect branch={}. There should not be a branch.'
                    .format(self.branch))
            if self.srcrev == 'AUTOINC':
                warnings.append('Incorrect {}={}. Should have fixed SHA.'
                                .format(self.srcrevkey, '${AUTOREV}'))
            reason = "{} is not one the team's branched repos.".format(
                self.reponame)

        if warnings:
            self.recipe.warn('Invalid recipe for {}.\n{}\nReason: {}'
                             .format(self.reponame,
                                     '\n'.join(warnings),
                                     reason))
        if fatal_errors:
            self.recipe.fatal('Invalid recipe for {}.\n{}\nReason: {}'
                              .format(self.reponame,
                                      '\n'.join(fatal_errors),
                                      reason))


    def validate_devenv(self, workingdir):
        from git import git
        repo = git(workingdir)
        repo.validate_against_repodef(self)



any_gitre = re.compile(r'git(sm)?://')
def is_gitpkg(recipe):
    src_uri = recipe.getVar('SRC_URI')
    return bool(any_gitre.search(src_uri))


class gitpkg(object):
    """
    Represents a set of git repos extracted from a SRC_URI.

    There will be one primary an optional list of submodules.
    This class should not be instantiated unless you know the SRC_URI
    contains a git:// entry.
    """
    def __init__(self, recipe):
        self.recipe = recipe
        src_uri = recipe.getVar('SRC_URI')
        self.repodef = None
        self.submodules = {}

        for piece in src_uri.split():
            if any_gitre.match(piece):
                repodef = gitpkg_repodef(piece, recipe)
                if repodef.subdir is None:
                    if self.repodef is not None:
                        recipe.fatal('Multiple git:// repos in SRC_URI')
                    self.repodef = repodef
                elif repodef.subdir in self.submodules:
                    recipe.fatal('Multiple gitsm:// repos in SRC_URI with same subdir: {}'
                                 .format(repodef.subdir))
                else:
                    self.submodules[repodef.subdir] = repodef
        if self.repodef is None:
            recipe.fatal('No git:// repos in SRC_URI')


    def validate_for_team(self, team):
        self.repodef.validate_for_team(team)
        for sub in self.submodules.itervalues():
            sub.validate_for_team(team)


    def validate_devenv(self):
        name = self.recipe.name
        devenv_path = os.path.join(ops_defs.topdir, 'src', name)
        if not os.path.isdir(devenv_path):
            logger.warning("{} doesn't exist.\n"
                           'But {} is in .devenv'
                           .format(devenv_path, name))
        else:
            self.repodef.validate_devenv(devenv_path)

            for sub in self.submodules.itervalues():
                devenv_subdir = os.path.join(devenv_path, sub.subdir)
                if not os.path.isdir(devenv_subdir):
                    logger.warning("{} doesn't exist.\n"
                                   "But it's a git submodule of {} which is in .devenv"
                                   .format(devenv_subdir, name))
                else:
                    sub.validate_devenv(devenv_subdir)
