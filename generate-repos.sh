#!/bin/bash
set -e
kojipath="https://kojipkgs.fedoraproject.org"
sync_repo() {
    mkdir -p $1
    pushd $1
    local repopath="$kojipath/repos/$1-build/latest/x86_64"
    local jars=$(dnf repoquery --quiet --repofrompath "$1,$repopath" \
                    --disablerepo \* --available --enablerepo $1 \
                    --archlist x86_64,noarch --file '*.jar' \
                    --qf="%{relativepath}"| sort -u)
    local files=""
    for jar in $jars; do
        local filename=`basename "$jar"`
        files="$filename"$'\n'"$files"
        if [ ! -f "$filename" ]; then
            wget -nv "$kojipath/$jar"
        fi
    done
    comm -13 <(sort <<< "$files") <(ls -1 *.rpm | sort) | xargs --no-run-if-empty rm
    popd
}

for repo; do
    sync_repo $repo
    wait # don't run multiple JVMs
    java-deptools build --collection $repo $repo &
done
wait
