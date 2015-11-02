#!/bin/bash
set -e
VERSION=0
RELNO="$(git rev-list "$(git describe --tags --abbrev=0)..HEAD" --count)"
shell='ssh root@java-deptools.fedorainfracloud.org'
cmd='git ls-tree -r HEAD |awk "/ blob /{print\$4}"'
{ eval "$cmd" && git submodule foreach "$cmd | sed s@^@\$path/@"; } | \
    sed '/^Entering /d' | xargs tar --xform "s@^@java-deptools-$VERSION/@S" -cf java-deptools-$VERSION.tar.gz
rm -r build
mkdir build
cd build
sed "s/^Release:[^%]*/&.$RELNO/" ../java-deptools.spec > java-deptools.spec
rpmbuild -bb -D"_sourcedir $PWD/.." -D"_rpmdir $PWD" java-deptools.spec
cat noarch/java-deptools-$VERSION*.noarch.rpm | $shell '
set -e
cat > java-deptools.rpm
dnf reinstall -y java-deptools.rpm
systemctl restart java-deptools-frontend
'
