# ovsdb-tool
bin_PROGRAMS += ovsdb/ovsdb-tool
ovsdb_ovsdb_tool_SOURCES = ovsdb/ovsdb-tool.c
ovsdb_ovsdb_tool_LDADD = lib/libovscommon.la ovsdb/libovsdb.la
# ovsdb-tool.1
man_MANS += ovsdb/ovsdb-tool.1
DISTCLEANFILES += ovsdb/ovsdb-tool.1
MAN_ROOTS += ovsdb/ovsdb-tool.1.in

# ovsdb-client
bin_PROGRAMS += ovsdb/ovsdb-client
ovsdb_ovsdb_client_SOURCES = ovsdb/ovsdb-client.c
ovsdb_ovsdb_client_LDADD = lib/libovscommon.la ovsdb/libovsdb.la
# ovsdb-client.1
man_MANS += ovsdb/ovsdb-client.1
DISTCLEANFILES += ovsdb/ovsdb-client.1
MAN_ROOTS += ovsdb/ovsdb-client.1.in

# ovsdb-server
sbin_PROGRAMS += ovsdb/ovsdb-server
ovsdb_ovsdb_server_SOURCES = ovsdb/ovsdb-server.c
ovsdb_ovsdb_server_LDADD = lib/libovscommon.la ovsdb/libovsdb.la
# ovsdb-server.1
man_MANS += ovsdb/ovsdb-server.1
DISTCLEANFILES += ovsdb/ovsdb-server.1
MAN_ROOTS += ovsdb/ovsdb-server.1.in

# ovsdb-idlc
noinst_SCRIPTS += ovsdb/ovsdb-idlc
EXTRA_DIST += ovsdb/ovsdb-idlc.in
MAN_ROOTS += ovsdb/ovsdb-idlc.1
DISTCLEANFILES += ovsdb/ovsdb-idlc
SUFFIXES += .ovsidl .ovsschema
OVSDB_IDLC = $(run_python) $(srcdir)/ovsdb/ovsdb-idlc.in
.ovsidl.c:
	$(AM_V_GEN)$(OVSDB_IDLC) c-idl-source $< > $@.tmp && mv $@.tmp $@
.ovsidl.h:
	$(AM_V_GEN)$(OVSDB_IDLC) c-idl-header $< > $@.tmp && mv $@.tmp $@

EXTRA_DIST += $(OVSIDL_BUILT)
BUILT_SOURCES += $(OVSIDL_BUILT)

# This must be done late: macros in targets are expanded when the
# target line is read, so if this file were to be included before some
# other file that added to OVSIDL_BUILT, then those files wouldn't get
# the dependency.
#
# However, current versions of Automake seem to output all variable
# assignments before any targets, so it doesn't seem to be a problem,
# at least for now.
$(OVSIDL_BUILT): ovsdb/ovsdb-idlc.in

# ovsdb-doc
EXTRA_DIST += ovsdb/ovsdb-doc
OVSDB_DOC = $(run_python) $(srcdir)/ovsdb/ovsdb-doc

# ovsdb-dot
EXTRA_DIST += ovsdb/ovsdb-dot.in ovsdb/dot2pic
noinst_SCRIPTS += ovsdb/ovsdb-dot
DISTCLEANFILES += ovsdb/ovsdb-dot
OVSDB_DOT = $(run_python) $(srcdir)/ovsdb/ovsdb-dot.in
