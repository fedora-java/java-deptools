package org.fedoraproject.javadeptools.data;

import java.util.Iterator;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.fedoraproject.javadeptools.model.PackageCollection;

public class PackageCollectionDao {
    EntityManager em;

    @Inject
    public PackageCollectionDao(EntityManager em) {
        this.em = em;
    }

    public PackageCollection getCollectionByName(String name) {
        Iterator<PackageCollection> it = em
                .createQuery(
                        "from PackageCollection where name = ?0 and finalized = true",
                        PackageCollection.class)
                .setParameter(0, name).getResultList().iterator();
        if (it.hasNext())
            return it.next();
        return null;
    }

    public PackageCollection startNewCollection(
            PackageCollection collection) {
        em.getTransaction().begin();
        Query query = em
                .createQuery("delete from PackageCollection where name = ?0 and finalized = false");
        query.setParameter(0, collection.getName());
        query.executeUpdate();
        em.persist(collection);
        em.getTransaction().commit();
        return collection;
    }

    public void finalizeCollection(PackageCollection collection) {
        em.getTransaction().begin();
        // cascades to packages
        Query query = em
                .createQuery("delete from PackageCollection where name = ?0 and finalized = true");
        query.setParameter(0, collection.getName());
        query.executeUpdate();
        collection.setFinalized(true);
        em.getTransaction().commit();
    }
}
