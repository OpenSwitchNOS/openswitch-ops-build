# Copyright (C) 2016 Hewlett Packard Enterprise Development LP
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

.PHONY: branch_create branch_add branch_remove branch_checkout branch_list_components
.PHONY: branch_rebase branch_merge

BRANCH_TOOL=$(BUILD_ROOT)/tools/bin/branch-tool.sh

ifeq (branch_create,$(firstword $(MAKECMDGOALS)))
  $(eval $(call PARSE_TWO_ARGUMENTS,branch_create))
  export NEW_BRANCH?=$(EXTRA_ARGS_1)
  export COMPONENTS?=$(EXTRA_ARGS_2)
  ifeq ($(NEW_BRANCH),)
   $(error ====== NEW_BRANCH variable is empty, please specify the name of the new branch =====)
  endif
  ifeq ($(COMPONENTS),)
   $(error ====== COMPONENTS variable is empty, please specify which component you want on the new branch =====)
  endif
endif
branch_create:
	$(V) $(BRANCH_TOOL) create $(NEW_BRANCH) $(COMPONENTS)

branch_add:
	$(V) $(BRANCH_TOOL) add $(COMPONENTS)

PARENT_BRANCH ?= master
branch_remove:
	$(V) $(BRANCH_TOOL) remove ${PARENT_BRANCH} $(COMPONENTS)

branch_checkout:
	$(V) $(BRANCH_TOOL) checkout $(NEW_BRANCH)

branch_list_components:
	$(V) $(BRANCH_TOOL) list_components

branch_rebase:
	$(V) $(BRANCH_TOOL) rebase

branch_merge:
	$(V) $(BRANCH_TOOL) merge $(NEW_BRANCH)
