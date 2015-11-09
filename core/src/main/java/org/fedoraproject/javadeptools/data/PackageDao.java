package org.fedoraproject.javadeptools.data;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;

import org.fedoraproject.javadeptools.Query;
import org.fedoraproject.javadeptools.model.Package;
import org.fedoraproject.javadeptools.model.PackageCollection;

import com.google.inject.persist.Transactional;

public class PackageDao {
    Provider<EntityManager> em;

    @Inject
    public PackageDao(Provider<EntityManager> em) {
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

        public NameComponent(String name, boolean glob) {
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
        TableQuery<Package> query = new TableQuery<>(Package.class, em.get());
        query.addComponent(new BaseComponent(collection));
        return query;
    }

    public TableQuery<Package> queryPackagesByName(
            PackageCollection collection, String nameGlob) {
        TableQuery<Package> query = new TableQuery<>(Package.class, em.get());
        query.addComponent(new BaseComponent(collection));
        query.addComponent(new NameComponent(nameGlob));
        return query;
    }

    @Transactional
    public Package persist(Package pkg) {
        em.get().persist(pkg);
        return pkg;
    }
}
