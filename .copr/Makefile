#!/usr/bin/make -f

spec := java-deptools.spec
outdir := $(CURDIR)
tmpdir := build
gitdir := $(dir $(spec))/.git

rev := $(shell ref="$$(cat $(gitdir)/HEAD)"; [[ "$$ref" == "ref: "* ]] && ref="$$(cat "$(gitdir)/$${ref\#ref: }")"; echo "$${ref:0:7}")
date := $(shell date +%Y%m%d.%H%M)

version := $(shell sed -n '/Version:/{s/.* //;p}' $(spec))
release := $(date).git.$(rev)
sbt_version := $(shell sed -n '/%global\s\+sbt_version\s\+/s///p' $(spec))

srpm: $(outdir)/java-deptools-$(version)-$(release).src.rpm

$(tmpdir)/java-deptools-$(version)-$(release).spec: $(spec)
	@mkdir -p $(tmpdir)
	sed '/^Release:/s/\(: *\).*/\1$(release)%{?dist}/' $< >$@

$(tmpdir)/java-deptools-$(version).tar.gz: $(gitdir)/..
	@mkdir -p $(tmpdir)
	tar c --exclude-vcs --exclude-vcs-ignores -C $< --transform 's|^\.|java-deptools-$(version)|' . | gzip -9 >$@

$(outdir)/java-deptools-$(version)-$(release).src.rpm: $(tmpdir)/java-deptools-$(version)-$(release).spec $(tmpdir)/java-deptools-$(version).tar.gz $(tmpdir)/sbt-$(sbt_version).tgz
	@mkdir -p $(outdir)
	rpmbuild -D'_srcrpmdir $(outdir)' -D'_sourcedir $(tmpdir)' -bs $(tmpdir)/java-deptools-$(version)-$(release).spec

$(tmpdir)/sbt-$(sbt_version).tgz:
	@mkdir -p $(tmpdir)
	curl -L https://github.com/sbt/sbt/releases/download/v$(sbt_version)/sbt-$(sbt_version).tgz -o $@

.PHONY: srpm

