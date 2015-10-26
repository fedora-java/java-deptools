package org.fedoraproject.javadeptools.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class TableQuery<T> extends AbstractQuery<T> {
    private Class<T> clazz;
    private EntityManager em;
    private List<QueryComponent<T>> components = new ArrayList<>();

    public TableQuery(Class<T> clazz, EntityManager em) {
        this.clazz = clazz;
        this.em = em;
    }

    public void addComponent(QueryComponent<T> component) {
        components.add(component);
    }

    private <E> void setGenericQueryProperties(CriteriaBuilder cb,
            CriteriaQuery<E> cq, Root<T> root, boolean fetch) {
        List<Predicate> preds = new ArrayList<>();
        for (QueryComponent<T> component : components) {
            component.initialize(cb, root);
            component.setJoins(fetch);
            for (Predicate pred : component.getPredicates()) {
                preds.add(pred);
            }
        }
        Predicate[] predsArray = new Predicate[preds.size()];
        cq.where(preds.toArray(predsArray));
    }

    private <E> void setQueryParameters(TypedQuery<E> query) {
        for (QueryComponent<T> component : components) {
            component.setParameters(query);
        }
    }

    @Override
    public List<T> getResults(int offset, int limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(clazz);
        Root<T> root = cq.from(clazz);
        cq.select(root);
        setGenericQueryProperties(cb, cq, root, true);
        List<Order> orders = new ArrayList<>();
        for (QueryComponent<T> component : components) {
            for (Order order : component.getOrder()) {
                orders.add(order);
            }
        }
        cq.orderBy(orders);
        TypedQuery<T> query = em.createQuery(cq);
        setQueryParameters(query);
        return Collections.unmodifiableList(query.setFirstResult(offset)
                .setMaxResults(limit).getResultList());
    }

    @Override
    public long getTotal() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<T> root = cq.from(clazz);
        cq.select(cb.count(root));
        setGenericQueryProperties(cb, cq, root, false);
        TypedQuery<Long> query = em.createQuery(cq);
        setQueryParameters(query);
        return query.getSingleResult();
    }
}
