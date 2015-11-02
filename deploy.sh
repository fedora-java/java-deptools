#!/bin/bash
set -e
VERSION=0
RELNO="$(git rev-list "$(git describe --tags --abbrev=0)..HEAD" --count)"
shell='ssh root@java-deptools.fedorainfracloud.org'
git archive HEAD --prefix="java-deptools-$VERSION/" | gzip > java-deptools-$VERSION.tar.gz
mkdir -p build
cd build
sed "s/^Release:[^%]*/&.$RELNO/" ../java-deptools.spec > java-deptools.spec
rpmbuild -bb -D"_sourcedir $PWD/.." -D"_rpmdir $PWD" java-deptools.spec
cat noarch/java-deptools-$VERSION*.noarch.rpm | $shell '
cat > java-deptools.rpm
dnf reinstall -y java-deptools.rpm
'
