package org.fedoraproject.javadeptools.impl;

import java.util.Collections;
import java.util.Map;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.jpa.JpaPersistModule;

public class JavaDeptoolsModule extends AbstractModule {

    @Override
    protected void configure() {
    }

    public static Injector createInjector(String persistenceUnit, Map<String, String> dbProps) {
        JpaPersistModule jpaPersistModule = new JpaPersistModule(
                persistenceUnit);
        jpaPersistModule.properties(dbProps);
        Injector injector = Guice.createInjector(new JavaDeptoolsModule(),
                jpaPersistModule);
        injector.getInstance(PersistenceInitializer.class);
        return injector;
    }

    public static Injector createInjector(String persistenceUnit) {
        return createInjector(persistenceUnit, Collections.emptyMap());
    }

    public static Injector createInjector(Map<String, String> dbProps) {
        return createInjector("org.fedoraproject.javadeptools", dbProps);
    }
}
