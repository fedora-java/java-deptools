package org.fedoraproject.javadeptools.data;

import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.fedoraproject.javadeptools.model.PackageCollection;

import com.google.inject.persist.Transactional;

public class PackageCollectionDao {
    Provider<EntityManager> em;

    @Inject
    public PackageCollectionDao(Provider<EntityManager> em) {
        this.em = em;
    }

    public PackageCollection getCollectionByName(String name) {
        Iterator<PackageCollection> it = em
                .get()
                .createQuery(
                        "from PackageCollection where name = ?0 and finalized = true",
                        PackageCollection.class)
                .setParameter(0, name).getResultList().iterator();
        if (it.hasNext())
            return it.next();
        return null;
    }

    @Transactional
    public PackageCollection startNewCollection(
            PackageCollection collection) {
        // delete possible unfinished collection
        Query query = em
                .get()
                .createQuery("delete from PackageCollection where name = ?0 and finalized = false");
        query.setParameter(0, collection.getName());
        query.executeUpdate();
        em.get().persist(collection);
        return collection;
    }

    @Transactional
    public void finalizeCollection(PackageCollection collection) {
        // cascades to packages
        Query query = em
                .get()
                .createQuery("delete from PackageCollection where name = ?0 and finalized = true");
        query.setParameter(0, collection.getName());
        query.executeUpdate();
        collection.setFinalized(true);
    }
}
