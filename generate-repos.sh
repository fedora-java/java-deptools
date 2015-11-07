#!/bin/bash
set -e
kojipath="https://kojipkgs.fedoraproject.org"
sync_repo() {
    mkdir -p $1
    pushd $1
    local repopath="$kojipath/repos/$1-build/latest/x86_64"
    local java_pkgs=$(repoquery --repoid $1 --repofrompath "$1,$repopath" \
                      --archlist x86_64,noarch --whatprovides '**.jar' \
                      --qf="%{base_package_name}#%{name}#%{version}#%{release}#%{arch}"| sort -u)
    for pkg in $java_pkgs; do
        IFS='#' read srcname name version release arch <<< "$pkg"
        filename="$name-$version-${release}.${arch}.rpm"
        if [ ! -f "$filename" ]; then
            wget -q "$kojipath/packages/$srcname/$version/$release/$arch/$filename"
        fi
    done
    popd
}

for repo; do
    sync_repo $repo
    wait # don't run multiple JVMs
    java-deptools --database jdbc:postgresql:java-deptools --collection $repo build $repo &
done
wait
