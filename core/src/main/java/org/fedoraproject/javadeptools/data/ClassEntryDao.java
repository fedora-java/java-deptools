package org.fedoraproject.javadeptools.data;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;

import org.fedoraproject.javadeptools.Query;
import org.fedoraproject.javadeptools.model.ClassEntry;
import org.fedoraproject.javadeptools.model.PackageCollection;

public class ClassEntryDao {
    private EntityManager em;

    @Inject
    public ClassEntryDao(EntityManager em) {
        this.em = em;
    }

    private static class BaseComponent extends QueryComponent<ClassEntry> {
        private PackageCollection collection;
        private ParameterExpression<PackageCollection> collectionParameter;
        private Join<Object, Object> packageJoin;

        public BaseComponent(PackageCollection collection) {
            this.collection = collection;
        }

        @Override
        public Predicate[] getPredicates() {
            collectionParameter = cb.parameter(PackageCollection.class);
            return new Predicate[] { cb.equal(
                    packageJoin.get("packageCollection"), collectionParameter) };
        }

        @Override
        public <E> void setParameters(TypedQuery<E> query) {
            query.setParameter(collectionParameter, collection);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void setJoins(boolean fetch) {
            // Ugly hack to prevent spurious joins in ugly Criteria API
            if (fetch) {
                packageJoin = (Join<Object, Object>) root.fetch("fileArtifact").fetch("pkg");
            } else {
                packageJoin = root.join("fileArtifact").join("pkg");
            }
        }

        @Override
        public Order[] getOrder() {
            return new Order[] { cb.asc(root.get("className")),
                    cb.asc(root.get("packageName")) };
        }
    }

    private static class NameComponent extends QueryComponent<ClassEntry> {
        private String glob;
        private ParameterExpression<String> globParameter;

        public NameComponent(String glob) {
            this.glob = glob;
        }

        @Override
        public Predicate[] getPredicates() {
            globParameter = cb.parameter(String.class);
            return new Predicate[] { xlike(root.get("className"),
                    globParameter) };
        }

        @Override
        public <E> void setParameters(TypedQuery<E> query) {
            query.setParameter(globParameter, glob);
        }
    }

    public Query<ClassEntry> queryClassEntriesByName(
            PackageCollection collection, String glob) {
        TableQuery<ClassEntry> query = new TableQuery<>(ClassEntry.class, em);
        query.addComponent(new BaseComponent(collection));
        query.addComponent(new NameComponent(glob));
        return query;
    }

}
