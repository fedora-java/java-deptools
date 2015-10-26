package org.fedoraproject.javadeptools.test;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManager;

import org.fedoraproject.javadeptools.data.PackageDao;
import org.fedoraproject.javadeptools.model.Package;
import org.fedoraproject.javadeptools.model.PackageCollection;
import org.junit.Before;
import org.junit.Test;

public class PackageDaoTest extends AbstractTest {

    private PackageDao packageDao;
    private PackageCollection collection;

    @Before
    public void setUp() {
        packageDao = injector.getInstance(PackageDao.class);
        EntityManager em = emf.createEntityManager();
        collection = em.find(PackageCollection.class, 1L);
    }

    @Test
    public void testGetPackage() {
        Package pkg = packageDao.getPackageByName(collection, "rnv");
        assertEquals("rnv", pkg.getName());
    }

}
