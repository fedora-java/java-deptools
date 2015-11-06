package org.fedoraproject.javadeptools.data;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;

import org.fedoraproject.javadeptools.Query;
import org.fedoraproject.javadeptools.model.ManifestEntry;
import org.fedoraproject.javadeptools.model.PackageCollection;

public class ManifestEntryDao {

    @Inject
    private EntityManager em;

    private static class BaseComponent extends QueryComponent<ManifestEntry> {
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
                packageJoin = (Join<Object, Object>) root.fetch("fileArtifact")
                        .fetch("pkg");
            } else {
                packageJoin = root.join("fileArtifact").join("pkg");
            }
        }
    }

    private static class ManifestComponent extends
            QueryComponent<ManifestEntry> {
        private String headerGlob;
        private String valueGlob;
        private ParameterExpression<String> headerParameter;
        private ParameterExpression<String> valueParameter;

        public ManifestComponent(String headerGlob, String valueGlob) {
            this.headerGlob = headerGlob;
            this.valueGlob = valueGlob;
        }

        @Override
        public Predicate[] getPredicates() {
            headerParameter = cb.parameter(String.class);
            valueParameter = cb.parameter(String.class);
            return new Predicate[] { cb.like(root.get("key"), headerParameter),
                    cb.like(root.get("value"), valueParameter) };
        }

        @Override
        public <E> void setParameters(TypedQuery<E> query) {
            query.setParameter(headerParameter, headerGlob);
            query.setParameter(valueParameter, valueGlob);
        }
    }

    public Query<ManifestEntry> queryByManifest(PackageCollection collection,
            String headerGlob, String valueGlob) {
        TableQuery<ManifestEntry> query = new TableQuery<>(ManifestEntry.class,
                em);
        query.addComponent(new BaseComponent(collection));
        query.addComponent(new ManifestComponent(headerGlob, valueGlob));
        return query;
    }
}
