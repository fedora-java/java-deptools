package org.fedoraproject.javadeptools.data;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public abstract class QueryComponent<T> {
    protected CriteriaBuilder cb;
    protected Root<T> root;

    public void initialize(CriteriaBuilder cb, Root<T> root) {
        this.cb = cb;
        this.root = root;
    }

    public void setJoins(boolean fetch) {
    }

    public Predicate[] getPredicates() {
        return new Predicate[] {};
    }

    public <E> void setParameters(TypedQuery<E> query) {

    }

    public Order[] getOrder() {
        return new Order[] {};
    }

}