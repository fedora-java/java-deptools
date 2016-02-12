#!/bin/bash
set -e
kojipath="https://kojipkgs.fedoraproject.org"
sync_repo() {
    mkdir -p $1
    pushd $1
    local repopath="$kojipath/repos/$1-build/latest/x86_64"
    local java_pkgs=$(repoquery --repoid $1 --repofrompath "$1,$repopath" \
                      --archlist x86_64,noarch --whatprovides '**.jar' \
                      --qf="%{relativepath}"| sort -u)
    local files=""
    for pkg in $java_pkgs; do
        local filename=`basename "$pkg"`
        files="$filename"$'\n'"$files"
        if [ ! -f "$filename" ]; then
            wget -nv "$kojipath/$pkg"
        fi
    done
    comm -13 <(sort <<< "$files") <(ls -1 *.rpm | sort) | xargs --no-run-if-empty rm
    popd
}

for repo; do
    sync_repo $repo
    wait # don't run multiple JVMs
    java-deptools --database jdbc:postgresql:java-deptools --collection $repo build $repo &
done
wait
