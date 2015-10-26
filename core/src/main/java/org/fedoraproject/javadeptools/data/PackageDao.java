package org.fedoraproject.javadeptools.data;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;

import org.fedoraproject.javadeptools.Query;
import org.fedoraproject.javadeptools.model.Package;
import org.fedoraproject.javadeptools.model.PackageCollection;

public class PackageDao {
    EntityManager em;

    @Inject
    public PackageDao(EntityManager em) {
        this.em = em;
    }

    private static class BaseComponent extends QueryComponent<Package> {
        private PackageCollection collection;
        private ParameterExpression<PackageCollection> collectionParameter;

        public BaseComponent(PackageCollection collection) {
            this.collection = collection;
        }

        @Override
        public Predicate[] getPredicates() {
            collectionParameter = cb.parameter(PackageCollection.class);
            return new Predicate[] { cb.equal(root.get("packageCollection"),
                    collectionParameter) };
        }

        @Override
        public <E> void setParameters(TypedQuery<E> query) {
            query.setParameter(collectionParameter, collection);
        }
    }

    private static class NameComponent extends QueryComponent<Package> {
        private String name;
        private boolean glob;
        private ParameterExpression<String> nameParameter;

        public NameComponent(String name,
                boolean glob) {
            this.name = name;
            this.glob = glob;
        }

        @Override
        public Predicate[] getPredicates() {
            nameParameter = cb.parameter(String.class);
            Predicate pred;
            if (glob) {
                pred = cb.like(root.get("name"), nameParameter);
            } else {
                pred = cb.equal(root.get("name"), nameParameter);
            }
            return new Predicate[] { pred };
        }

        @Override
        public <E> void setParameters(TypedQuery<E> query) {
            query.setParameter(nameParameter, name);
        }
    }

    public Query<Package> getAllPackages(PackageCollection collection) {
        TableQuery<Package> query = new TableQuery<>(Package.class, em);
        query.addComponent(new BaseComponent(collection));
        return query;
    }

    public Package getPackageByName(
            PackageCollection collection, String name) {
        TableQuery<Package> query = queryPackagesByName(collection,
                name, false);
        // TODO exception
        return query.getResults(1).get(0);
    }

    private TableQuery<Package> queryPackagesByName(
            PackageCollection collection, String name, boolean glob) {
        TableQuery<Package> query = new TableQuery<>(Package.class, em);
        query.addComponent(new BaseComponent(collection));
        query.addComponent(new NameComponent(name, glob));
        return query;
    }

    public Query<Package> queryPackagesByNameGlob(
            PackageCollection collection, String nameGlob) {
        return queryPackagesByName(collection, nameGlob, true);
    }

    public Package persist(Package pkg) {
        em.getTransaction().begin();
        em.persist(pkg);
        em.getTransaction().commit();
        return pkg;
    }
}
