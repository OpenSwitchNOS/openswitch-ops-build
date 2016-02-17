import logging
import sys
import re

class MultilineFormatter(object):
    def format(self, record):
        msg = '\n    '.join(record.msg.splitlines())
        name = re.sub('^ops.', '', record.name)
        return '{}:{}:{}'.format(record.levelname, name, msg)


dflt_level = logging.WARNING

def config(output=sys.stdout, level=dflt_level):
    config.logger = logging.getLogger('ops')
    console = logging.StreamHandler(output)
    console.setFormatter(MultilineFormatter())
    config.logger.addHandler(console)
    config.logger.setLevel(level)


def getLogger(name):
    if not hasattr(config, 'logger'):
        raise RuntimeError('{m}.getLogger() called before {m}.config()'
                           .format(m=__name__))
    return config.logger.getChild(name)
