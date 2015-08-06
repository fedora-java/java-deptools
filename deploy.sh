#!/bin/bash
shell='ssh java-deptools@java-deptools.fedorainfracloud.org'
activator frontend/dist
$shell '
pkill -f "^java.*frontend-0" || :
rm -rf frontend-0/
dd of=frontend-0.zip
unzip frontend-0.zip
nohup frontend-0/bin/frontend -Djava-deptools.db.url=jdbc:h2:file:/home/java-deptools/prod &> /dev/null &' \
    < frontend/target/universal/frontend-0.zip
