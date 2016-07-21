# spec for java-deptools server deployment with bundled libs
Name:           java-deptools
Version:        0
Release:        1%{?dist}
Summary:        Tool for analysis of Java RPMS
License:        ASL 2.0
BuildArch:      noarch

AutoReqProv:    no
Requires:       java-headless

Source0:        %{name}-%{version}.tar.gz

%define __jar_repack %{nil}

%description
%{summary}.

%prep
%setup -q

%build
activator dist
unzip core/target/universal/%{name}-core-%{version}.zip
unzip frontend/target/universal/%{name}-frontend-%{version}.zip

%install
mkdir -p %{buildroot}%{_javadir}/%{name}
mkdir -p %{buildroot}%{_sysconfdir}/%{name}
mkdir -p %{buildroot}%{_sharedstatedir}/%{name}/repos
mkdir -p %{buildroot}%{_unitdir}
cp -pr java-deptools-core-%{version}/lib/* %{buildroot}%{_javadir}/%{name}/
cp -pr java-deptools-frontend-%{version}/lib/* %{buildroot}%{_javadir}/%{name}/

%jpackage_script org.fedoraproject.javadeptools.Cli '' '' %{name} %{name} 1
%global args "-Dconfig.file=%{_sysconfdir}/%{name}/application.conf -Dlogger.file=%{_sysconfdir}/%{name}/logback.xml"
%jpackage_script play.core.server.ProdServerStart '' %{args} %{name} %{name}-frontend 1

install -m755 generate-repos.sh %{buildroot}%{_bindir}/java-deptools-repogen
install -m644 java-deptools-frontend.service %{buildroot}%{_unitdir}/
mkdir -p %{buildroot}%{_datadir}/%{name}
install -m644 core/src/main/resources/schema.sql %{buildroot}%{_datadir}/%{name}/
install -m644 frontend/conf/application.conf %{buildroot}%{_sysconfdir}/%{name}/
install -m644 frontend/conf/logback.xml %{buildroot}%{_sysconfdir}/%{name}/
mkdir -p %{buildroot}%{_localstatedir}/log/%{name}

%pre
getent group %{name} >/dev/null || groupadd -r %{name}
getent passwd %{name} >/dev/null || \
    useradd -r -g %{name} -d %{_sharedstatedir}/%{name} -s /bin/sh \
    -c "Runs %{name} services" %{name}
exit 0

%post
%systemd_post %{name}-frontend.service

%preun
%systemd_preun %{name}-frontend.service

%postun
%systemd_postun %{name}-frontend.service

%files
%{_bindir}/%{name}*
%{_datadir}/%{name}
%{_javadir}/%{name}
%{_sysconfdir}/%{name}
%attr(755, %{name}, %{name}) %{_localstatedir}/log/%{name}
%attr(755, %{name}, %{name}) %{_sharedstatedir}/%{name}
%{_unitdir}/%{name}-frontend.service
