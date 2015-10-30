#!/bin/bash
set -e
shell='ssh root@java-deptools.fedorainfracloud.org'
activator core/rpm:packageBin frontend/rpm:packageBin
tar cj {core,frontend}/target/rpm/RPMS/noarch/java-deptools-*-0-1.noarch.rpm \
       ./././././generate-repos.sh | \
$shell '
set -e
tar xj  --strip-components 5
systemctl stop java-deptools-frontend.service ||:
dnf install -y java-deptools-{core,frontend}-0-1.noarch.rpm
rm -f java-deptools-{core,frontend}-0-1.noarch.rpm
mv generate-repos.sh /usr/libexec/
systemctl start java-deptools-frontend.service'
