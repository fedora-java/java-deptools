package org.fedoraproject.javadeptools.impl;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

public class CustomDDLInitializer {
    @Inject
    private Provider<EntityManager> em;

    @Transactional
    public void initializeIndices() {
        em.get().createNativeQuery("CREATE INDEX classEntry_name ON classentry(className)").executeUpdate();
        em.get().createNativeQuery("CREATE INDEX classEntry_nameLower ON classentry(lower(className))").executeUpdate();
        em.get().createNativeQuery("CREATE INDEX classEntry_nameCompound ON classentry((packageName||'.'||className))")
                .executeUpdate();
        em.get().createNativeQuery(
                "CREATE INDEX classEntry_nameCompoundLower ON classentry((lower(packageName||'.'||className)))")
                .executeUpdate();
    }

    public void initialize() {
        try {
            initializeIndices();
        } catch (PersistenceException e) {
            // the indices already exist or we're on a DB that doesn't support it
        }
    }
}
