package org.fedoraproject.javadeptools.impl;

import java.util.Map;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.fedoraproject.javadeptools.Database;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class JavaDeptoolsModule extends AbstractModule {
    public JavaDeptoolsModule(Map<String, String> dbProperties) {
        this.dbProperties = dbProperties;
    }

    private Map<String, String> dbProperties;

    @Provides
    @Singleton
    public EntityManagerFactory provideEntityManagerFactory() {
        return Persistence.createEntityManagerFactory(
                "org.fedoraproject.javadeptools", dbProperties);
    }

    @Provides
    public EntityManager provideEntityManager(EntityManagerFactory emf) {
        return emf.createEntityManager();
    }

    @Override
    protected void configure() {
        bind(Database.class).to(DefaultDatabase.class);
    }
}
