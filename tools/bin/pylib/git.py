import os
import subprocess
import urlparse
import re

import ops_logging

logger = ops_logging.getLogger(__name__)

def cached(func):
    def inner(*args, **kwargs):
        refresh = kwargs.pop('refresh', False)
        key = args
        if kwargs:
            key += ('__SEP__') + tuple(sorted(kwds))
        if refresh or key not in inner.cache:
            inner.cache[key] = func(*args, **kwargs)
        return inner.cache[key]
    inner.cache = dict()
    return inner


class SmartUrl(object):
    def __init__(self, url):
        self.url = url
        self._parsed = urlparse.urlparse(url)

    def __str__(self):
        return self.url

    def __getattr__(self, attr):
        return getattr(self._parsed, attr)

    @property
    @cached
    def relpath(self):
        return self.path.lstrip(os.path.sep)


class git(object):
    @classmethod
    def _invoke(cls, *args):
        cargs = ['git'] + [a for a in args]
        logger.debug('{}'.format(cargs))
        lines = subprocess.check_output(cargs).splitlines()
        return lines[0] if len(lines)==1 else lines

    @classmethod
    def _bool_query(cls, *args):
        cargs = ['git'] + [a for a in args]
        logger.debug('{}'.format(cargs))
        with open(os.devnull, 'w') as NULL:
            return subprocess.call(cargs, stdout=NULL, stderr=NULL) == 0

    """Object for querying git"""
    def __init__(self, path):
        self.gitdir = git._invoke('-C', path, 'rev-parse', '--git-dir')
        if not self.gitdir.startswith('/'):
            self.gitdir = os.path.join(path, self.gitdir)
        self.workdir = os.path.dirname(self.gitdir)


    def invoke(self, *args):
        return git._invoke('-C', self.gitdir, *args)

    def bool_query(self, *args):
        return git._bool_query('-C', self.gitdir, *args)

    def rev_parse(self, *args):
        return self.invoke('rev-parse', *args)

    def merge_base(self, rev1, rev2):
        return self.invoke('merge-base', rev1, rev2)

    @cached
    def cur_config(self):
        cfg = {}
        for line in self.invoke('config', '--list'):
            k, v = re.split(r'\s*=\s*', line, maxsplit=1)
            cfg[k] = v
        review_cfg = os.path.join(self.workdir, '.gitreview')
        if os.path.exists(review_cfg):
            for line in self.invoke('config', '--file', review_cfg, '--list'):
                k, v = re.split(r'\s*=\s*', line, maxsplit=1)
                cfg[k] = v
        return cfg

    def get_config(self, key):
        return self.cur_config()[key]

    def has_config(self, key):
        return key in self.cur_config()

    @cached
    def is_detached_head(self):
        return self.rev_parse('--abbrev-ref', 'HEAD') == 'HEAD'

    @cached
    def cur_branch(self):
        bname = self.rev_parse('--abbrev-ref', 'HEAD')
        if bname == 'HEAD':
            # detached head state, return SHA instead
            bname = self.rev_parse('HEAD')
        return bname

    @cached
    def remote_url(self, name):
        return SmartUrl(self.get_config('remote.'+name+'.url'))

    @property
    def origin(self):
        return self.remote_url('origin')

    def has_remote(self, name):
        return self.has_config('remote.'+name+'.url')

    @property
    def gerrit(self):
        return self.remote_url('gerrit')

    def validate_gerrit(self, default_branch):
        want_gerrit_host = re.sub(r'^git', 'review', self.origin.hostname)
        norm_origin_path = re.sub(r'\.git$', '', self.origin.relpath)

        if self.has_remote('gerrit'):
            # already have a gerrit remote, let's validate against it
            norm_gerrit_path = re.sub(r'\.git$', '', self.gerrit.relpath)
            if (self.gerrit.hostname != want_gerrit_host
                or
                norm_gerrit_path != norm_origin_path):
                logger.warning('Incorrect gerrit remote {}.\n'
                               'Origin remote is {}.\n'
                               'Workdir = {}'
                               .format(self.gerrit, self.origin,
                                       self.workdir))
        elif self.has_config('gerrit.host'):
            # no gerrit remote, but we do have a .gitreview file
            gerrit_host = self.get_config('gerrit.host')
            norm_gerrit_path = re.sub(r'\.git$', '',
                                      self.get_config('gerrit.project'))
            if gerrit_host != want_gerrit_host:
                logger.warning('Incorrect entry in .gitreview: host={}.\n'
                               'Expected host={}.\n'
                               'Workdir = {}'
                               .format(gerrit_host, want_gerrit_host,
                                       self.workdir))
            if norm_gerrit_path != norm_origin_path:
                logger.warning('Incorrect entry in .gitreview: project={}.\n'
                               'Expected project={}.\n'
                               'Workdir = {}'
                               .format(norm_gerrit_path, norm_origin_path,
                                       self.workdir))
        else:
            # no gerrit remote, and not .gitreview file
            logger.warning('Missing gerrit remote and .gitreview file.\n'
                           'Workdir = {}'
                           .format(self.workdir))

        if self.has_config('gerrit.defaultbranch'):
            gerrit_dftb = self.get_config('gerrit.defaultbranch')
            if default_branch:
                if gerrit_dftb != default_branch:
                    logger.warning('Incorrect entry in .gitreview: defaultbranch={}.\n'
                                   'Expected defaultbranch={}.\n'
                                   'Workdir = {}'
                                   .format(gerrit_dftb, default_branch,
                                           self.workdir))
            else:
                logger.warning('.gitreview specifies defaultbranch={}\n'
                               'Expected no defaultbranch.\n'
                               'Workdir = {}'
                               .format(gerrit_dftb, self.workdir))
        elif default_branch:
            logger.warning('No defaultbranch specified in .gitreview\n'
                           'Expected defaultbranch={}.\n'
                           'Workdir = {}'
                           .format(default_branch, self.workdir))


    def validate_against_repodef(self, repodef):
        errors = []
        warnings = []

        if self.origin.hostname != repodef.hostname:
            errors.append(
                'Incorrect origin hostname: {}. Should be: {}.'
                .format(self.origin.hostname, repodef.hostname))

        if repodef.branch:
            if self.cur_branch() != repodef.branch:
                warnings.append(
                    'Incorrect current branch: {}. Should be: {}.'
                    .format(self.cur_branch(), repodef.branch))
        elif repodef.srcrev != 'AUTOINC':
            # make sure we're in a detached head state and that our SHA
            # is the one it's supposed to be
            if not self.is_detached_head():
                warnings.append(
                    'Expected detached head. Found current branch: {}.'
                    .format(self.cur_branch()))
            elif self.cur_branch() != repodef.srcrev:
                warnings.append(
                    'Incorrect fixed SHA: {}.\n'
                    'Should be:           {}.'
                    .format(self.cur_branch(), repodef.srcrev))
        else:
            # Don't report this problem again. We already reported it
            # with the recipe
            pass

        if errors or warnings:
            if warnings:
                logger.warning('Invalid git config {}.\n{}'
                               .format(self.workdir,
                                       '\n'.join(warnings)))
            if errors:
                logger.error('Invalid git config {}.\n{}'
                             .format(self.workdir,
                                     '\n'.join(errors)))
        else:
            self.validate_gerrit(default_branch=repodef.branch)
