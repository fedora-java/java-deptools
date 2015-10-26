package org.fedoraproject.javadeptools.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.fedoraproject.javadeptools.impl.PersistenceInitializer;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.jpa.JpaPersistModule;

public abstract class AbstractTest {
    protected static Injector injector;
    protected static EntityManagerFactory emf;

    @BeforeClass
    public static void setUpClass0() throws Exception {
        injector = Guice.createInjector(new JpaPersistModule(
                "org.fedoraproject.javadeptools.test"));
        injector.getInstance(PersistenceInitializer.class);
        emf = injector.getInstance(EntityManagerFactory.class);
    }

    @Before
    public void setUp0() throws Exception {
        executeResourceScript("/create_test_data.sql");
    }

    @After
    public void tearDown0() throws Exception {
        executeResourceScript("/delete_test_data.sql");
    }

    protected void executeResourceScript(String resourcePath)
            throws IOException {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try (InputStream is = getClass().getResourceAsStream(resourcePath);
                Scanner s = new Scanner(is)) {
            s.useDelimiter(";");
            while (s.hasNext()) {
                em.createNativeQuery(s.next()).executeUpdate();
            }
        }
        em.getTransaction().commit();
        em.close();
    }
}
