.PHONY: ##PLATFORM##

include yocto/*/meta-platform-$(DISTRO)-##PLATFORM##/Rules.make

all_platforms:: ##PLATFORM##

##PLATFORM##::
	$(V) echo ##PLATFORM## > .multi_last_platform
	$(V) @$(ECHO) "$(PURPLE)Multi-target run building ##PLATFORM##...$(GRAY)" ; \
	  if [ -n "$(MULTIBUILD_OUTPUT_CMD)" ] ; then \
	    $(ECHO) "Output log for ##PLATFORM## at $(MULTIBUILD_OUTPUT)...\n" ; \
	  fi
	$(V)$(MAKE) -j1 $(##PLATFORM_UPPER##_ALL_TARGETS) MULTIPLATFORM=##PLATFORM## $(MULTIBUILD_OUTPUT_CMD)
