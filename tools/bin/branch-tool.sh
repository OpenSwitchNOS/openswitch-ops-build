#!/bin/bash

#This is actually "reset"
reset='\033[0m'
red='\033[1;31m'
blue='\033[1;34m'
purple='\033[1;35m'
cyan='\033[1;36m'
gerrit_remote=gerrit
review_site=review.openswitch.net

error() {
    echo
    echo -e "${red}Error:${reset} ${1}"
}

fatal_error() {
    error "$@"
    echo
    exit 1
}

confirm () {
    read -r -p "${1:-Are you sure? [y/N]} " response
    case $response in
        [yY][eE][sS]|[yY])
            true
            ;;
        *)
            false
            ;;
    esac
}

git_fetch_latest() {
    if ! git remote | grep -wq "$gerrit_remote " ; then
        echo -e "Setting up gerrit..."
        git review -s
    fi
    echo
    echo -e "Fetching latest from upstream to validate branch operation..."
    git fetch gerrit
}

git_check_current_branch_is_clean() {
    # Verify no unstaged changes
    if git status | grep -q 'modified:' ; then
        fatal_error "You have unstaged changes in your current branch. Please stash them."
    fi
}

git_check_ready_for_new_branch() {
    git_fetch_latest
    # Verify the branch doesn't exist already remotely or locally
    if git branch -a | grep -wq "remotes/$gerrit_remote/$1\$" ; then
        fatal_error "The branch '$1' already exists on gerrit"
    fi
    if git branch | grep -wq "$1\$" ; then
        fatal_error "The branch '$1' already exists locally"
    fi
    git_check_current_branch_is_clean
}

verify_components_status_before_change() {
    # Verify we find a recipe for each of the COMPONENTS
    # if is on the devenv, then check is ready for changing branch
    for component in ${@:2} ; do
        search_expression="find ${BUILD_ROOT}/yocto -name ${component}.bb"
        lines=$(${search_expression} | wc -l)
        if [ $lines -eq 0 ] ; then
            fatal_error "Could not find a recipe for component ${component}..."
        fi
        if [ $lines -ne 1 ] ; then
            error "Found more than one recipe for ${component}..."
            echo
            ${search_expression}
            exit 1
        fi
        if grep -q "^${component}\$" ${BUILD_ROOT}/.devenv ; then
            pushd . > /dev/null
            cd ${BUILD_ROOT}/src/${component}
            git_check_current_branch_is_clean $1
            popd > /dev/null
        fi
    done
}

verify_components_status_before_add() {
    # Verify we find a recipe for each of the COMPONENTS
    # if is on the devenv, then check is ready for a new branch
    for component in ${@:2} ; do
        echo
        echo "Validating ${component}..."
        search_expression="find ${BUILD_ROOT}/yocto -name ${component}.bb"
        lines=$(${search_expression} | wc -l)
        if [ $lines -eq 0 ] ; then
            fatal_error "Could not find a recipe for component ${component}..."
        fi
        if [ $lines -ne 1 ] ; then
            error "Found more than one recipe for ${component}..."
            echo
            ${search_expression}
            exit 1
        fi
        # Check is not already added
        if [ -n "$3" ] && echo $3 | grep -q $lines ; then
            fatal_error "Component $component already member of the branch"
        fi
        if grep -q "^${component}\$" ${BUILD_ROOT}/.devenv ; then
            pushd . > /dev/null
            cd ${BUILD_ROOT}/src/${component}
            git_check_ready_for_new_branch $1
            popd > /dev/null
        else
            recipe_url=$(query-recipe.py --gitrepo ${component})
            if git ls-remote ${recipe_url} | grep -wq "$1\$" ; then
                fatal_error "The branch '$1' already exists in upstream"
            fi
        fi
    done
}

add_components() {
    for component in ${@:2} ; do
        if grep -q "^${component}\$" ${BUILD_ROOT}/.devenv ; then
            pushd . > /dev/null
            cd ${BUILD_ROOT}/src/${component}
        else
            pushd . > /dev/null
            mkdir -p ${BUILD_ROOT}/.branch-tool
            cd ${BUILD_ROOT}/.branch-tool
            $(query-recipe.py -s -v SRCREV --gitrepo --gitbranch ${component})
            if ! git clone -q --single-branch -b $gitbranch $gitrepo ${component} ; then
                echo git clone -q --single-branch -b $gitbranch $gitrepo ${component}
                fatal_error "Failed to clone ${component} to create the branch"
            fi
            cd ${component}
            git review -s
        fi
        git checkout -b $1
        git push -u $gerrit_remote $1
        popd > /dev/null
        if ! grep -q "^${component}\$" ${BUILD_ROOT}/.devenv ; then
            rm -Rf ${BUILD_ROOT}/.branch-tool/${component}
        fi
    done
    # We do this in two cycles, since otherwise the change on the autorev may crash
    # bitbake runs for following modules until the changes propagate to the git mirrors
    for component in ${@:2} ; do
        recipe=$(find ${BUILD_ROOT}/yocto -name ${component}.bb)
        # Check if we have recent code
        if grep -q 'BRANCH ?=' ${recipe} ; then
            sed -i -e "s#^BRANCH ?=.*#BRANCH ?= \"${1}\"#" ${recipe}
        else
            sed -i -e "s#;branch=.* #;branch=${1} #" ${recipe}
        fi
        sed -i -e "s#^SRCREV.*#SRCREV = \"\${AUTOREV}\"#" ${recipe}
        git add ${recipe}
    done
}

create() {
    echo
    echo -e "${blue}Creating a new branch '$1'...${reset}\n"
    echo "Running sanity checks..."
    # Check if the ops-build repo is ready
    git_check_ready_for_new_branch $1
    verify_components_status_before_add ${@} ""
    # Now create the branch locally of the current branch
    echo -e "\nSanity checks passed, adding the branches across the repos...\n"
    # Modify the recipes to point to this branch and use auto-rev
    # Create the branch on the components
    git checkout -b $1
    add_components ${@}
    git commit -s -m "Configuring branch $1"
    git push -u $gerrit_remote $1

    return 0
}

add() {
    current_branch=$(git rev-parse --abbrev-ref HEAD)

    echo -e "${blue}Adding components to branch '${current_branch}'...${reset}\n"
    echo "Running sanity checks..."

    recipes=$(get_recipes_for_branch $current_branch)

    # Check the branch in ops-build
    git_check_current_branch_is_clean
    # Check the components to add
    verify_components_status_before_add $current_branch ${@} ${recipes}
    echo -e "\nSanity checks passed, adding the branches across the repos...\n"
    add_components $current_branch ${@}
    git commit -s -m "Added components to the branch: ${@}"
    git push -u $gerrit_remote $current_branch
}

remove_component() {
    current_branch = $1
    component = $2
    parent_branch =$3

    recipes=$(get_recipes_for_branch $current_branch)
    recipe=""

    for tmp in ${recipes} ; do
        comp=$(basename ${tmp} .bb)
        if [ "$comp" == "$component" ] ; then
            recipe=${tmp}
        fi
    done

    if [ -z "$recipe" ] ; then
        fatal_error "Component $component is not part of this branch"
    fi

    if grep -q "^${component}\$" ${BUILD_ROOT}/.devenv ; then
        echo "About to remove the component from the devenv, this may result in data lost for unsaved changes..."
        confirm || fatal_error "Aborted"
        make devenv_rm ${component}
    fi
    # Check if we have recent code
    if grep -q 'BRANCH ?=' ${recipe} ; then
        new_branch=$(make bake RECIPE="-e" | grep OPS_REPO_BRANCH= | cut -f2 -d\")
        echo "Reseting $component to branch \${OPS_REPO_BRANCH} ($new_branch)..."
        sed -i -e "s#^BRANCH *= *\"${current_branch}\"#BRANCH ?= \"\${OPS_REPO_BRANCH}\"#" ${recipe}
    else
        echo "Reseting $component to branch $parent_branch..."
        sed -i -e "s#;branch=${current_branch}#;branch=${parent_branch}#" ${recipe}
        new_branch="$parent_branch"
    fi
    recipe_url = $(query-recipe.py --gitrepo ${component})
    lastest_rev = $(git ls-remote ${recipe_url} | grep $new_branch | cut -f1)
    sed -i -e "s#^SRCREV.*#SRCREV = \"${lastest_rev}\"#" ${recipe}
    git add ${recipe}
    git commit -s -m "Reset component ${component} back into branch $new_branch (latest head)"
}

remove() {
    current_branch=$(git rev-parse --abbrev-ref HEAD)
    parent_branch = $1

    echo -e "${blue}Removing components from branch '${current_branch}'...${reset}\n"

    for component in ${@:2} ; do
        remove_component $current_branch $component $parent_branch
    done
    git push -u $gerrit_remote $1
}

get_recipes_for_branch() {
    current_branch=$1

    if [ "$current_branch" == "master " ] ; then
        fatal_error "Cannot identify branch components on master"
    fi

    # Collect possible recipes
    recipes=$(grep -lR "^BRANCH *= *\"$current_branch\"\$" ${BUILD_ROOT}/yocto 2>/dev/null)
    recipes="$recipes $(grep -lR ";branch=$current_branch" ${BUILD_ROOT}/yocto 2>/dev/null)"

    echo "${recipes}"
}

list_components() {
    current_branch=$(git rev-parse --abbrev-ref HEAD)

    recipes=$(get_recipes_for_branch $current_branch)

    echo
    echo -e "${blue}Listing components for branch '${current_branch}'...${reset}\n"
    echo
    if [ -n "$recipes" ] ; then
        echo " No components found for this branch"
    fi
    for comp in ${recipes} ; do
        echo "  * $(basename ${comp} .bb)"
    done
    echo
}

checkout() {
    current_branch=$(git rev-parse --abbrev-ref HEAD)

    echo "Running sanity checks..."
    # Check the branch in ops-build
    git_check_current_branch_is_clean
}

rebase() {
    current_branch=$(git rev-parse --abbrev-ref HEAD)
}

merge() {
    current_branch=$(git rev-parse --abbrev-ref HEAD)
}

help() {
    echo
	echo "Syntax: branch-tool.sh <command> <cmd_parameters>"
    echo "   Commands: help, create, add"
    echo
}

if [ $# -lt 1 ]; then
    help
    exit 1
fi

if [ -z "${BUILD_ROOT}" ] ; then
    fatal_error "This script should be invoked by the makefile system"
fi

cd ${BUILD_ROOT}

case "$1" in
create)
	create ${@:2}
	;;
add)
    add ${@:2}
    ;;
remove)
    remove ${@:2}
    ;;
checkout)
    checkout ${@:2}
    ;;
list_components)
    list_components
    ;;
rebase)
    rebase ${@:2}
    ;;
merge)
    merge ${@:2}
    ;;
help)
    help
    ;;
*)
    echo
    echo "Unknown command '$1'"
    echo
	exit 1
esac
