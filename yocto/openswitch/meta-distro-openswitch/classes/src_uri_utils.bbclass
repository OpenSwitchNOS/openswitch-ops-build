# Utilities for updating SRC_URIs in a recipe without having
# to tweak the original SRC_URI= line.
#
# When working with feature branch sets, it's common to need to modify
# SRC_URI defintions in recipe files (to add branch=, to change hostnames,
# etc.). But direct modification of those SRC_URI= lines (and
# particuarly the SRCREV= lines) leads to guaranteed conflicts. These
# conflicts have proven themselves to be a major source of trouble
# when teams take merges from master.
#
# The routines in this file allow recipes to be edited in a way that
# significantly reduces merge conflicts. Blocks of lines can be added
# anywhere in the recipe file after the SRC_URI= and SRCREV=
# lines. Here are some examples:
#
# ----- ops-cli.bbappend -----
#
# ...
#
# ########## BEGIN feature branch overrides ##########
# inherit src_uri_utils
# def update_srcuri(d):
#     return set_feature_branch(d.getVar('SRC_URI', True),
#                               'openswitch/ops-cli',
#                               'git-nos.rose.rdlabs.hpecorp.net', 'feature/acl')
#
# SRC_URI := "${@update_srcuri(d)}"
# SRCREV = "${AUTOREV}"
# ########## END feature branch overrides ##########
#
# ...
#
#
#
# ----- ops.bbappend -----
#
# ...
#
# ########## BEGIN feature branch overrides ##########
# inherit src_uri_utils
# def update_srcuri(d):
#     src_uri = d.getVar('SRC_URI', True)
#     src_uri = set_feature_branch(src_uri,
#         'hpe/hpe-schema-extensions',
#         'git-nos.rose.rdlabs.hpecorp.net', 'feature/acl')
#     src_uri = set_no_branch(src_uri, 'openswitch/ops', 'git.openswitch.net')
#     return src_uri
#
# SRC_URI := "${@update_srcuri(d)}"
# SRCREV_hpenos = "${AUTOREV}"
# ########## END feature branch overrides ##########
#
# ...
#


def maybe_rewrite_git_uri(gituri, repo, host, branch):
    """
    Maybe modify a git URI so the supplied repo uses host and branch.

    :param piece:  The git uri to modify
    :param repo:   The repo whose host & branch we want to change
    :param host:   The host we want to use
    :param branch: The branch we want to use
    :return:       Returns the (possibly) modified git uri in SRC_URI format
    """
    import re
    import urlparse

    # git uris from SRC_URI have many pieces separated by ';'
    parts = re.split(';', gituri)

    # see if this git uri is for tour target repo
    if not re.search(repo+'(\.git)?$', parts[0]):
        return gituri

    # update the host portion of the uri
    parsed = urlparse.urlparse(parts[0])
    if parsed.port is not None:
        parsed = parsed._replace(netloc="{}:{}".format(host,
                                                       parsed.port))
    else:
        parsed = parsed._replace(netloc=host)
    uri = parsed.geturl()

    # build up a dict of all the var=value parts
    extras = {}
    for p in parts[1:]:
        k, v = re.split('=', p)
        extras[k] = v

    # set or clear "branch=" depending on supplied branch
    if branch is not None:
        extras['branch'] = branch
    elif 'branch' in extras:
        del extras['branch']

    # bring all the modified parts back together again
    return ';'.join([uri] + ['{}={}'.format(k,v) for k,v in extras.iteritems()])



def set_feature_branch(src_uri, repo, host, branch):
    """
    Modify the supplied SRC_URI string to update host and branch for repo

    :param src_uri: The SRC_URI string
    :param repo:    The repo whose host & branch we want to change
    :param host:    The host we want to use
    :param branch:  The branch we want to use
    :return:        Returns the modified SRC_URI string
    """
    import re

    # SRC_URI strings can have multiple uris separated by whitespace
    pieces = []
    for piece in src_uri.split():
        if re.match(r'git(sm)?://', piece):
            # git uri: try to rewrite it
            pieces.append(maybe_rewrite_git_uri(piece, repo, host, branch))
        else:
            # non-git: use it as is
            pieces.append(piece)

    return ' '.join(pieces)


def set_no_branch(src_uri, repo, host):
    return set_feature_branch(src_uri, repo, host, None)
