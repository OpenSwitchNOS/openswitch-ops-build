
###RECIPE##
.PHONY: ##RECIPE##-build ##RECIPE##-clean ##RECIPE##-coverage ##RECIPE##-deploy ##RECIPE##-undeploy

##RECIPE##-build:
	$(V)$(call DEVTOOL, build ##RECIPE##)

##RECIPE##-clean:
	$(V)$(call BITBAKE, -c cleansstate ##RECIPE##)

##RECIPE##-reconfigure:
	$(V)$(call BITBAKE, -f -c configure ##RECIPE##)

$(eval $(call PARSE_ARGUMENTS,##RECIPE##-deploy))
TARGET?=$(EXTRA_ARGS)
ifneq ($(findstring ##RECIPE##-deploy,$(MAKECMDGOALS)),)
  ifeq ($(TARGET),)
    $(error ====== TARGET variable is empty, please specify where to deploy =====)
  endif
endif
##RECIPE##-deploy:
	$(V)$(call DEVTOOL, deploy-target -s ##RECIPE## $(TARGET))

$(eval $(call PARSE_ARGUMENTS,##RECIPE##-undeploy))
TARGET?=$(EXTRA_ARGS)
ifneq ($(findstring ##RECIPE##-undeploy,$(MAKECMDGOALS)),)
  ifeq ($(TARGET),)
    $(error ====== TARGET variable is empty, please specify where to undeploy from  =====)
  endif
endif
##RECIPE##-undeploy:
	$(V)$(call DEVTOOL, undeploy-target -s ##RECIPE## $(TARGET))

##RECIPE##-nfs-deploy:
	$(V)$(call DEVTOOL, deploy-target -s ##RECIPE## localhost:$(NFSROOTPATH))

##RECIPE##-nfs-undeploy:
	$(V)$(call DEVTOOL, undeploy-target -s ##RECIPE## localhost:$(NFSROOTPATH))

#Coverage options. Can be overwritten at src/##RECIPE##/Rules-ops-build.make
MODULE_NAME=##RECIPE##
COVERAGE_BASE_DIR=$(BUILD_ROOT)/src/##RECIPE##/build/
COVERAGE_REPORT_DIR=$(COVERAGE_BASE_DIR)/coverage

-include src/##RECIPE##/Rules-ops-build.make

#MODULE_TEST_TARGET should be defined at src/##RECIPE##/Rules-ops-build.make
##RECIPE##-coverage: coverage-setup $(MODULE_TEST_TARGET) coverage-report
#END_##RECIPE##
