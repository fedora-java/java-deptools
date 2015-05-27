package org.fedoraproject.javadeptools.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.fedoraproject.javadeptools.Database;
import org.fedoraproject.javadeptools.Package;
import org.fedoraproject.javadeptools.impl.DefaultDatabase;
import org.fedoraproject.javadeptools.impl.PersistentPackage;
import org.junit.Before;
import org.junit.Test;

public class DatabaseTest {
    private EntityManager em;
    private Database db;

    @Before
    public void setUp() throws Exception {
        EntityManagerFactory factory = Persistence
                .createEntityManagerFactory("org.fedoraproject.javadeptools.test");
        em = factory.createEntityManager();
        db = new DefaultDatabase(em);
    }

    @Test
    public void testGetPackages() {
        PersistentPackage p = new PersistentPackage("foo");
        em.getTransaction().begin();
        em.persist(p);
        em.getTransaction().commit();
        Collection<Package> actual = db.getPackages();
        assertNotNull(actual);
        assertEquals(1, actual.size());
        Package pa = actual.iterator().next();
        assertEquals("foo", pa.getName());
    }

}
