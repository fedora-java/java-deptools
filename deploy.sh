#!/bin/bash
shell='ssh root@java-deptools.fedorainfracloud.org'
activator frontend/rpm:packageBin
$shell '
set -e
systemctl stop java-deptools-frontend.service ||:
dd of=frontend.rpm
dnf install -y frontend.rpm
systemctl start java-deptools-frontend.service' \
    < frontend/target/rpm/RPMS/noarch/java-deptools-frontend-0-1.noarch.rpm
