package org.fedoraproject.javadeptools.data;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public abstract class QueryComponent<T> {
    protected CriteriaBuilder cb;
    protected Root<T> root;
    private boolean caseSensitive;

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

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    protected Predicate xlike(Expression<String> expr1, Expression<String> expr2) {
        if (caseSensitive)
            return cb.like(expr1, expr2);
        else
            return cb.like(cb.lower(expr1), cb.lower(expr2));
    }

}