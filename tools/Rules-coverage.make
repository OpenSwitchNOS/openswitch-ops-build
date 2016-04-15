# Copyright (C) 2015-2016 Hewlett Packard Enterprise Development LP
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# Coverage command
.PHONY: generate_coverage_report

$(eval $(call PARSE_ARGUMENTS,generate_coverage_report))
ifeq (generate_coverage_report,$(firstword $(MAKECMDGOALS)))
  $(eval $(call PARSE_TWO_ARGUMENTS,generate_coverage_report))
  COV_MODULE?=$(EXTRA_ARGS_1)
  ifeq ($(COV_MODULE),)
    $(error ====== COV_MODULE variable is empty, please specify which package you want =====)
  endif
endif

generate_coverage_report:
	$(V)echo "===================  Generating Coverage Report  =================== "
	$(V) $(BUILD_ROOT)/tools/bin/generate_coverage_report.sh
